package com.ssambbong.gymjjak.organization.organization.application.service;

import com.ssambbong.gymjjak.organization.organization.application.command.OrganizationUpdateCommand;
import com.ssambbong.gymjjak.organization.organization.application.port.OrganizationMetricsPort;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationCommandUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
        organizationMetricsPort.recordOrganizationUpdated();
    }
}
