package com.ssambbong.gymjjak.organization.organizationTrainer.application.service;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase.OrganizationTrainerQueryUseCase;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationTrainerQueryService implements OrganizationTrainerQueryUseCase {

    private final OrganizationTrainerRepository organizationTrainerRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public List<TrainerSummary> findMyOrganizationTrainers(Long organizationAccountId) {
        Organization organization = organizationRepository.findByOrganizationAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new);
        return organizationTrainerRepository.findTrainersByOrganizationId(organization.getOrganizationId());
    }
}
