package com.ssambbong.gymjjak.organization.organization.application.service;

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

    @Override
    @Transactional
    public void updateOrganization(Long organizationAccountId, String facilityPhone, String instagramUrl, String blogUrl, String websiteUrl) {

        Organization organization = organizationRepository.findByOrganizationAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new);

        Organization updated = organization.update(facilityPhone, instagramUrl, blogUrl, websiteUrl);
        organizationRepository.update(updated);
    }
}
