package com.ssambbong.gymjjak.dashboard.organization.application.service;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtClientResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtCourseResult;
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

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

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

        List<TrendPoint> weekly = toTrendPoints(
                springDataPtReservationRepository.findWeeklyUserTrendByOrganizationId(organizationId, oneYearAgo));
        List<TrendPoint> monthly = toTrendPoints(
                springDataPtReservationRepository.findMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo));
        List<TrendPoint> threeMonthly = toTrendPoints(
                springDataPtReservationRepository.findThreeMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo));
        List<TrendPoint> sixMonthly = toTrendPoints(
                springDataPtReservationRepository.findSixMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo));

        OrgTrendResult trend = new OrgTrendResult(weekly, monthly, threeMonthly, sixMonthly);

        return new OrgStatsResult(trainerCount, totalUserCount, currentUserCount, thisMonthRevenue, trend);
    }

    @Override
    public OrgSalesResult getSales(Long userId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(userId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        YearMonth thisMonth = YearMonth.now();
        LocalDateTime startOfMonth = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = thisMonth.plusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime startOfPrevMonth = thisMonth.minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime startOf7MonthsAgo = thisMonth.minusMonths(6).atDay(1).atStartOfDay();

        long totalRevenue = springDataPaymentRepository.sumTotalRevenueByOrganizationId(organizationId);
        long thisMonthRevenue = springDataPaymentRepository.sumThisMonthRevenueByOrganizationId(
                organizationId, startOfMonth, startOfNextMonth);
        long prevMonthRevenue = springDataPaymentRepository.sumThisMonthRevenueByOrganizationId(
                organizationId, startOfPrevMonth, startOfMonth);

        double monthOverMonthRate = prevMonthRevenue == 0 ? 0.0
                : Math.round((thisMonthRevenue - prevMonthRevenue) * 1000.0 / prevMonthRevenue) / 10.0;

        List<TrendPoint> monthlyRevenue = springDataPaymentRepository
                .findMonthlyRevenueByOrganizationId(organizationId, startOf7MonthsAgo)
                .stream()
                .map(r -> new TrendPoint(r.getDate(), r.getAmount()))
                .toList();

        long totalForRatio = springDataPaymentRepository
                .findTrainerRevenueByOrganizationId(organizationId, startOfMonth, startOfNextMonth)
                .stream().mapToLong(r -> r.getTotalAmount()).sum();

        List<TrainerSalesResult> trainerSales = springDataPaymentRepository
                .findTrainerRevenueByOrganizationId(organizationId, startOfMonth, startOfNextMonth)
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

        return new OrgSalesResult(totalRevenue, thisMonthRevenue, monthOverMonthRate, monthlyRevenue, trainerSales);
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
                        s.averageRating(),
                        s.clientCount()
                ))
                .toList();
    }

    private List<TrendPoint> toTrendPoints(List<TrendPointRow> rows) {
        return rows.stream()
                .map(r -> new TrendPoint(r.getDate(), r.getCount()))
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
