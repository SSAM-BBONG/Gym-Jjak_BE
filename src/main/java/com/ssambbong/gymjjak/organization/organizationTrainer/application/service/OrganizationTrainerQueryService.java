package com.ssambbong.gymjjak.organization.organizationTrainer.application.service;

import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase.OrganizationTrainerQueryUseCase;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository.TrainerSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationTrainerQueryService implements OrganizationTrainerQueryUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationTrainerRepository organizationTrainerRepository;

    @Override
    public List<TrainerSummary> findMyOrganizationTrainers(Long organizationAccountId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        return organizationTrainerRepository.findTrainersByOrganizationId(organizationId);
    }
}
