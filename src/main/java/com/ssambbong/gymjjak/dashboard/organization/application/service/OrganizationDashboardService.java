package com.ssambbong.gymjjak.dashboard.organization.application.service;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgTrendResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerClientResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrendPoint;
import com.ssambbong.gymjjak.dashboard.organization.application.usecase.OrganizationDashboardUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationDashboardService implements OrganizationDashboardUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationTrainerRepository organizationTrainerRepository;
    private final SpringDataPtReservationRepository springDataPtReservationRepository;

    @Override
    public OrgStatsResult getStats(Long userId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(userId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        long trainerCount = organizationTrainerRepository.countActiveByOrganizationId(organizationId);
        long totalUserCount = springDataPtReservationRepository.countDistinctUsersByOrganizationId(organizationId);
        long currentUserCount = springDataPtReservationRepository.countDistinctCurrentUsersByOrganizationId(organizationId);

        List<TrendPoint> weekly = springDataPtReservationRepository
                .findWeeklyUserTrendByOrganizationId(organizationId)
                .stream().map(r -> new TrendPoint(r.getDate(), r.getCount())).toList();
        List<TrendPoint> monthly = springDataPtReservationRepository
                .findMonthlyUserTrendByOrganizationId(organizationId)
                .stream().map(r -> new TrendPoint(r.getDate(), r.getCount())).toList();
        List<TrendPoint> threeMonthly = springDataPtReservationRepository
                .findThreeMonthlyUserTrendByOrganizationId(organizationId)
                .stream().map(r -> new TrendPoint(r.getDate(), r.getCount())).toList();
        List<TrendPoint> sixMonthly = springDataPtReservationRepository
                .findSixMonthlyUserTrendByOrganizationId(organizationId)
                .stream().map(r -> new TrendPoint(r.getDate(), r.getCount())).toList();

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
}
