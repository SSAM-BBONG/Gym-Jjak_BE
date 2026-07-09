package com.ssambbong.gymjjak.dashboard.organization.application.service;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtCourseResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgTrendResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerClientResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrendPoint;
import com.ssambbong.gymjjak.dashboard.organization.application.usecase.OrganizationDashboardUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.TrendPointRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationDashboardService implements OrganizationDashboardUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationTrainerRepository organizationTrainerRepository;
    private final SpringDataPtReservationRepository springDataPtReservationRepository;
    private final SpringDataPtCourseRepository springDataPtCourseRepository;

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

        List<TrendPoint> weekly = toTrendPoints(
                springDataPtReservationRepository.findWeeklyUserTrendByOrganizationId(organizationId, oneYearAgo));
        List<TrendPoint> monthly = toTrendPoints(
                springDataPtReservationRepository.findMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo));
        List<TrendPoint> threeMonthly = toTrendPoints(
                springDataPtReservationRepository.findThreeMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo));
        List<TrendPoint> sixMonthly = toTrendPoints(
                springDataPtReservationRepository.findSixMonthlyUserTrendByOrganizationId(organizationId, threeYearsAgo));

        OrgTrendResult trend = new OrgTrendResult(weekly, monthly, threeMonthly, sixMonthly);

        return new OrgStatsResult(trainerCount, totalUserCount, currentUserCount, trend);
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
}
