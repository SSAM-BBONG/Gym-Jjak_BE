package com.ssambbong.gymjjak.organization.organizationTrainer.application.service;

import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.port.OrganizationTrainerMetricsPort;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase.OrganizationTrainerCommandUseCase;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.exception.OrganizationTrainerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationTrainerCommandService implements OrganizationTrainerCommandUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationTrainerRepository organizationTrainerRepository;
    private final OrganizationTrainerMetricsPort organizationTrainerMetricsPort;

    @Override
    @Transactional
    public void removeTrainer(Long organizationAccountId, Long organizationTrainerId) {
        Long organizationId = organizationRepository.findByOrganizationAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new)
                .getOrganizationId();

        organizationTrainerRepository.findActiveByIdAndOrganizationId(organizationTrainerId, organizationId)
                .orElseThrow(OrganizationTrainerNotFoundException::new);

        organizationTrainerRepository.remove(organizationTrainerId);

        try {
            organizationTrainerMetricsPort.recordTrainerRemoved();
        } catch (Exception e) {
            log.warn("event=metrics_record_failed metric=trainer_removed", e);
        }
    }
}
