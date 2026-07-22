package com.ssambbong.gymjjak.dashboard.organization.application.service;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtClientResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtCourseResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgRevenueTrendResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgSalesResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgTrendResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerClientResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerSalesResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrendPoint;
import com.ssambbong.gymjjak.dashboard.organization.application.usecase.OrganizationDashboardUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.payments.payment.infrastructure.persistence.SpringDataPaymentRepository;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.TrendPointRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationDashboardService implements OrganizationDashboardUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationTrainerRepository organizationTrainerRepository;
    private final SpringDataPtReservationRepository springDataPtReservationRepository;
    private final SpringDataPtCourseRepository springDataPtCourseRepository;
    private final SpringDataPaymentRepository springDataPaymentRepository;

    @Override
    public OrgStatsResult getStats(Long userId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(userId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        long trainerCount = organizationTrainerRepository.countActiveByOrganizationId(organizationId);
        long totalUserCount = springDataPtReservationRepository.countDistinctUsersByOrganizationId(organizationId);
        long currentUserCount = springDataPtReservationRepository.countDistinctCurrentUsersByOrganizationId(organizationId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAgo = now.minusYears(1);
        LocalDateTime threeYearsAgo = now.minusYears(3);

        YearMonth thisMonth = YearMonth.now();
        LocalDateTime startOfMonth = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = thisMonth.plusMonths(1).atDay(1).atStartOfDay();
        long thisMonthRevenue = springDataPaymentRepository.sumThisMonthRevenueByOrganizationId(
                organizationId, startOfMonth, startOfNextMonth);

        List<TrendPoint> weekly = fillGaps(toTrendPoints(
                springDataPtReservationRepository.findWeeklyUserTrendByOrganizationId(organizationId, oneYearAgo)), weeklyDates());
        List<TrendPoint> monthly = fillGaps(toTrendPoints(
                springDataPtReservationRepository.findMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo)), monthlyDates());
        List<TrendPoint> threeMonthly = fillGaps(toTrendPoints(
                springDataPtReservationRepository.findThreeMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo)), threeMonthlyDates());
        List<TrendPoint> sixMonthly = fillGaps(toTrendPoints(
                springDataPtReservationRepository.findSixMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo)), sixMonthlyDates());

        OrgTrendResult trend = new OrgTrendResult(weekly, monthly, threeMonthly, sixMonthly);

        return new OrgStatsResult(trainerCount, totalUserCount, currentUserCount, thisMonthRevenue, trend);
    }

    @Override
    public OrgSalesResult getSales(Long userId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(userId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        YearMonth thisMonth = YearMonth.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = thisMonth.plusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime startOfPrevMonth = thisMonth.minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime oneYearAgo = now.minusYears(1);
        LocalDateTime threeYearsAgo = now.minusYears(3);

        long totalRevenue = springDataPaymentRepository.sumTotalRevenueByOrganizationId(organizationId);
        long thisMonthRevenue = springDataPaymentRepository.sumThisMonthRevenueByOrganizationId(
                organizationId, startOfMonth, startOfNextMonth);
        long prevMonthRevenue = springDataPaymentRepository.sumThisMonthRevenueByOrganizationId(
                organizationId, startOfPrevMonth, startOfMonth);

        double monthOverMonthRate = prevMonthRevenue == 0 ? 0.0
                : Math.round((thisMonthRevenue - prevMonthRevenue) * 1000.0 / prevMonthRevenue) / 10.0;

        List<TrendPoint> weeklyRevenue = fillGaps(toRevenueTrendPoints(
                springDataPaymentRepository.findWeeklyRevenueByOrganizationId(organizationId, oneYearAgo)), weeklyDates());
        List<TrendPoint> monthlyRevenue = fillGaps(toRevenueTrendPoints(
                springDataPaymentRepository.findMonthlyRevenueByOrganizationId(organizationId, threeYearsAgo)), monthlyDates());
        List<TrendPoint> threeMonthlyRevenue = fillGaps(toRevenueTrendPoints(
                springDataPaymentRepository.findThreeMonthlyRevenueByOrganizationId(organizationId, threeYearsAgo)), threeMonthlyDates());
        List<TrendPoint> sixMonthlyRevenue = fillGaps(toRevenueTrendPoints(
                springDataPaymentRepository.findSixMonthlyRevenueByOrganizationId(organizationId, threeYearsAgo)), sixMonthlyDates());

        OrgRevenueTrendResult revenueTrend = new OrgRevenueTrendResult(
                weeklyRevenue, monthlyRevenue, threeMonthlyRevenue, sixMonthlyRevenue);

        List<SpringDataPaymentRepository.TrainerRevenueRow> trainerRevenueRows =
                springDataPaymentRepository.findTrainerRevenueByOrganizationId(organizationId, startOfMonth, startOfNextMonth);

        long totalForRatio = trainerRevenueRows.stream().mapToLong(r -> r.getTotalAmount()).sum();

        List<TrainerSalesResult> trainerSales = trainerRevenueRows
                .stream()
                .map(r -> new TrainerSalesResult(
                        r.getTrainerProfileId(),
                        r.getTrainerName(),
                        r.getThisMonthAmount(),
                        r.getTotalAmount(),
                        totalForRatio == 0 ? 0.0
                                : Math.round(r.getTotalAmount() * 1000.0 / totalForRatio) / 10.0
                ))
                .toList();

        return new OrgSalesResult(totalRevenue, thisMonthRevenue, monthOverMonthRate, revenueTrend, trainerSales);
    }

    @Override
    public List<TrainerClientResult> getTrainerClients(Long userId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(userId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        return organizationTrainerRepository.findTrainerClientsByOrganizationId(organizationId)
                .stream()
                .map(s -> new TrainerClientResult(
                        s.trainerProfileId(),
                        s.trainerName(),
                        s.clientCount(),
                        s.ptCount()
                ))
                .toList();

    }

    private List<TrendPoint> toTrendPoints(List<TrendPointRow> rows) {
        return rows.stream()
                .map(r -> new TrendPoint(r.getDate(), r.getCount()))
                .toList();
    }

    private List<TrendPoint> toRevenueTrendPoints(List<SpringDataPaymentRepository.MonthlyRevenueRow> rows) {
        return rows.stream()
                .map(r -> new TrendPoint(r.getDate(), r.getAmount()))
                .toList();
    }

    private List<TrendPoint> fillGaps(List<TrendPoint> points, List<LocalDate> dates) {
        Map<LocalDate, Long> map = points.stream()
                .collect(Collectors.toMap(TrendPoint::date, TrendPoint::value));
        return dates.stream()
                .map(date -> new TrendPoint(date, map.getOrDefault(date, 0L)))
                .toList();
    }

    private List<LocalDate> weeklyDates() {
        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        return IntStream.range(0, 52)
                .mapToObj(i -> thisMonday.minusWeeks(51 - i))
                .toList();
    }

    private List<LocalDate> monthlyDates() {
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        return IntStream.range(0, 36)
                .mapToObj(i -> thisMonth.minusMonths(35 - i))
                .toList();
    }

    private List<LocalDate> threeMonthlyDates() {
        LocalDate now = LocalDate.now();
        int quarterStartMonth = ((now.getMonthValue() - 1) / 3) * 3 + 1;
        LocalDate thisQuarter = LocalDate.of(now.getYear(), quarterStartMonth, 1);
        return IntStream.range(0, 12)
                .mapToObj(i -> thisQuarter.minusMonths((long) (11 - i) * 3))
                .toList();
    }

    private List<LocalDate> sixMonthlyDates() {
        LocalDate now = LocalDate.now();
        int halfStartMonth = now.getMonthValue() <= 6 ? 1 : 7;
        LocalDate thisHalf = LocalDate.of(now.getYear(), halfStartMonth, 1);
        return IntStream.range(0, 6)
                .mapToObj(i -> thisHalf.minusMonths((long) (5 - i) * 6))
                .toList();
    }

    @Override
    public List<OrgPtCourseResult> getPtCourses(Long userId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(userId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        return springDataPtCourseRepository.findPtCoursesByOrganizationId(organizationId)
                .stream()
                .map(r -> new OrgPtCourseResult(
                        r.getPtCourseId(),
                        r.getTitle(),
                        r.getPrice(),
                        r.getTotalSessionCount(),
                        r.getStatus(),
                        r.getTrainerName(),
                        r.getCurrentStudentCount()
                ))
                .toList();
    }

    @Override
    public List<OrgPtClientResult> getPtClients(Long userId, Long ptCourseId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(userId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        return springDataPtReservationRepository.findPtClientsByPtCourseId(ptCourseId, organizationId)
                .stream()
                .map(r -> new OrgPtClientResult(
                        r.getUserName(),
                        r.getEnrolledAt(),
                        r.getProgressCount(),
                        r.getTotalSessionCount()
                ))
                .toList();
    }
}
