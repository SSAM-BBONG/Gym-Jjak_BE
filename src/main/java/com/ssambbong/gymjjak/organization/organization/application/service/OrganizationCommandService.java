package com.ssambbong.gymjjak.organization.organization.application.service;

import com.ssambbong.gymjjak.organization.organization.application.command.OrganizationUpdateCommand;
import com.ssambbong.gymjjak.organization.organization.application.port.OrganizationMetricsPort;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationCommandUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationCommandService implements OrganizationCommandUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMetricsPort organizationMetricsPort;

    @Override
    @Transactional
    public void updateOrganization(OrganizationUpdateCommand command) {

        Organization organization = organizationRepository.findByOrganizationAccountId(command.organizationAccountId())
                .orElseThrow(OrganizationNotFoundException::new);

        Organization updated = organization.update(command.facilityPhone(), command.instagramUrl(), command.blogUrl(), command.websiteUrl());
        organizationRepository.update(updated);
        recordMetricSafely(organizationMetricsPort::recordOrganizationUpdated, "recordOrganizationUpdated");
    }

    private void recordMetricSafely(Runnable metricCall, String metricName) {
        Runnable safeCall = () -> {
            try {
                metricCall.run();
            } catch (Exception e) {
                log.warn("메트릭 기록 실패 - metric: {}", metricName, e);
            }
        };
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    safeCall.run();
                }
            });
        } else {
            safeCall.run();
        }
    }
}
