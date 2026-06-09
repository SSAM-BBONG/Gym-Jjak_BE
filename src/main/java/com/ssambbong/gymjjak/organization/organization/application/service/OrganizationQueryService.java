package com.ssambbong.gymjjak.organization.organization.application.service;

import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationQueryUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationsResponse;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationQueryService implements OrganizationQueryUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final OrganizationTrainerRepository organizationTrainerRepository;

    @Override
    public Organization findMyOrganization(Long organizationAccountId) {
        return organizationRepository.findByOrganizationAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    @Override
    public List<FindOrganizationsResponse> findOrganizations() {
        return organizationRepository.findAll().stream()
                .map(org -> {
                    String loginId = organizationApplicationRepository
                            .findRequestedLoginIdByApplicationId(org.getApplicationId())
                            .orElse(null);
                    long trainerCount = organizationTrainerRepository.countActiveByOrganizationId(org.getOrganizationId());
                    return FindOrganizationsResponse.of(org, loginId, trainerCount);
                })
                .toList();
    }

}
