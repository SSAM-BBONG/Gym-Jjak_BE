package com.ssambbong.gymjjak.dashboard.admin.application.service;

import com.ssambbong.gymjjak.dashboard.admin.application.port.AdminRevenueQueryPort;
import com.ssambbong.gymjjak.dashboard.admin.application.port.MonthlyPaymentRevenue;
import com.ssambbong.gymjjak.dashboard.admin.application.query.*;
import com.ssambbong.gymjjak.dashboard.admin.application.usecase.AdminDashboardQueryUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organization.infrastructure.persistence.SpringDataOrganizationRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.infrastructure.persistence.SpringDataReportGroupRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminDashboardQueryService implements AdminDashboardQueryUseCase {

    // 현재 월 포함 최근 6개월
    private static final int MONTH_RANGE = 6;

    // PT 수수료율 10%
    private static final BigDecimal PT_COMMISSION_RATE = new BigDecimal("0.10");

    // payment 테이블 조회 Port
    private final AdminRevenueQueryPort adminRevenueQueryPort;

    private final SpringDataUserRepository userRepository;
    private final SpringDataTrainerProfileRepository trainerProfileRepository;
    private final SpringDataOrganizationRepository organizationRepository;
    private final SpringDataPtCourseRepository ptCourseRepository;
    private final SpringDataReportGroupRepository reportGroupRepository;
    private final Clock clock;


    @Override
    public AdminMemberStatisticsResult findMemberStatistics() {
        log.info("event=admin_dashboard_find_member_statistics_started");
        long totalUserCount = userRepository.countActiveUsersByRole(
                UserRole.USER
        );

        long totalTrainerCount = trainerProfileRepository.countByStatus(
                TrainerProfileStatus.ACTIVE
        );

        long totalOrganizationCount = organizationRepository.countByStatus(
                OrganizationStatus.ACTIVE
        );

        // 6개월 가입자 수 월별 반환
        List<MonthlyUserSignupResult> monthlyUserSignups =
                findRecentMonthlyUserSignups();

        log.info("event=admin_dashboard_find_member_statistics_succeeded");

        return AdminMemberStatisticsResult.builder()
                .totalUserCount(totalUserCount)
                .totalTrainerCount(totalTrainerCount)
                .totalOrganizationCount(totalOrganizationCount)
                .monthlyUserSignups(monthlyUserSignups)
                .build();
    }

    @Override
    public AdminContentStatisticsResult findContentStatistics() {
        log.info("event=admin_dashboard_find_content_statistics_started");

        long activePtCourseCount =
                ptCourseRepository.countByStatus(PtCourseStatus.VISIBLE);

        long blindedPtCourseCount =
                ptCourseRepository.countByStatus(PtCourseStatus.BLOCKED);

        long pendingReportGroupCount =
                reportGroupRepository.countByReviewStatusAndDeletedAtIsNull(
                        ReportGroupReviewStatus.PENDING
                );

        log.info(
                "event=admin_dashboard_find_content_statistics_succeeded, " +
                        "activePtCourseCount={}, " +
                        "blindedPtCourseCount={}, " +
                        "pendingReportGroupCount={}",
                activePtCourseCount,
                blindedPtCourseCount,
                pendingReportGroupCount
        );

        return AdminContentStatisticsResult.builder()
                .activePtCourseCount(activePtCourseCount)
                .blindedPtCourseCount(blindedPtCourseCount)
                .pendingReportGroupCount(pendingReportGroupCount)
                .build();
    }

    @Override
    public AdminRevenueStatisticsResult findRevenueStatistics() {
        log.info("event=admin_dashboard_find_revenue_statistics_started");

        // YearMonth : 연 - 월
        YearMonth currentMonth = YearMonth.now(clock);
        YearMonth startMonth = currentMonth.minusMonths(MONTH_RANGE - 1);

        // 시작 : 월 1일 0시 부터 시작
        LocalDateTime startDate = startMonth.atDay(1).atStartOfDay();
        // 종료 : 다음 달 1일 0시
        LocalDateTime endDate = currentMonth.plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        // 월별 매출 집계 결과 Map으로 변환
        Map<YearMonth, MonthlyPaymentRevenue> revenueByMonth =
                // payment 테이블에서 월별 금액 집계해서 반환
                adminRevenueQueryPort.findMonthlyPaymentRevenue(startDate, endDate)
                        .stream()
                        .collect(Collectors.toMap(
                                // LocalDate -> YearMonth 변환, key로 사용
                                revenue-> YearMonth.from(revenue.month()),
                                // 받은 객체 수정 없이 Map의 value로 사용
                                Function.identity()
                        ));

        // 원본 데이터 조회 값 -> Result 형태로 변환 단계
        List<MonthlyRevenueResult> monthlyRevenues = IntStream.range(0, MONTH_RANGE)
                // 6개월 생성
                .mapToObj(startMonth::plusMonths)
                // 월별 데이터 생성 내부 메서드 호출
                .map(month -> toMonthlyRevenueResult(
                        month,
                        // 위 객체에서 월 추출
                        revenueByMonth.get(month)
                ))
                .toList();

        log.info(
                "event=admin_dashboard_find_revenue_statistics_succeeded, startMonth={}, endMonth={}",
                startMonth,
                currentMonth
        );

        return new AdminRevenueStatisticsResult(monthlyRevenues);
    }

    // 월별 매출 금액을 관리자 매출 통계로 변환하는 내부 메서드
    private MonthlyRevenueResult toMonthlyRevenueResult(
            YearMonth month,
            MonthlyPaymentRevenue monthlyPaymentRevenue
    ) {
        // pt 매출
        long ptPaymentAmount = monthlyPaymentRevenue == null ? 0L : monthlyPaymentRevenue.ptPaymentAmount();

        // 구독권 매출
        long subscriptionRevenue = monthlyPaymentRevenue == null ? 0L : monthlyPaymentRevenue.subscriptionPaymentAmount();

        // 짐짝의 pt 수수료 매출 계산
        long ptCommissionRevenue = calculatePtCommission(ptPaymentAmount);

        return MonthlyRevenueResult.builder()
                .month(month.toString())
                .ptCommissionRevenue(ptCommissionRevenue)
                .subscriptionRevenue(subscriptionRevenue)
                .totalRevenue(ptCommissionRevenue + subscriptionRevenue)
                .build();

    }

    // 월별 PT 총매출에 수수료 적용 메서드
    private long calculatePtCommission(long ptPaymentAmount) {
        return BigDecimal.valueOf(ptPaymentAmount)
                // 10% 수수료 구하기
                .multiply(PT_COMMISSION_RATE)
                // 소수점 없이 반올림
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private List<MonthlyUserSignupResult> findRecentMonthlyUserSignups() {

        YearMonth currentMonth = YearMonth.now(clock);

        YearMonth startMonth = currentMonth.minusMonths(MONTH_RANGE - 1);

        // 시작 월
        LocalDateTime startDate = startMonth
                .atDay(1)
                .atStartOfDay();

        // 종료 월
        LocalDateTime endDate = currentMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        Map<String, Long> signupCountByMonth =
                userRepository.findMonthlyUserSignups(startDate, endDate)
                        .stream()
                        .collect(Collectors.toMap(
                                SpringDataUserRepository.MonthlyUserSignupRow::getMonth,
                                // 회원 수 null -> 0으로 변환
                                row -> row.getCount() == null ? 0L : row.getCount()
                        ));

        // 총 6개월 회원 수 return
        return IntStream.range(0, MONTH_RANGE)
                .mapToObj(startMonth::plusMonths)
                .map(month -> {
                    String monthKey = month.toString();

                    return MonthlyUserSignupResult.builder()
                            .month(monthKey)
                            .count(signupCountByMonth.getOrDefault(
                                    monthKey,
                                    0L
                            ))
                            .build();
                })
                .toList();


    }
}
