package com.ssambbong.gymjjak.dashboard.organization.application.service;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;
import com.ssambbong.gymjjak.dashboard.organization.application.usecase.OrganizationDashboardUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return new OrgStatsResult(trainerCount, totalUserCount, currentUserCount);
    }
}
