package com.ssambbong.gymjjak.dashboard.admin.application.service;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminContentStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminMemberStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.MonthlyUserSignupResult;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminDashboardQueryService implements AdminDashboardQueryUseCase {

    // 현재 월 포함 최근 6개월
    private static final int MONTH_RANGE = 6;

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
