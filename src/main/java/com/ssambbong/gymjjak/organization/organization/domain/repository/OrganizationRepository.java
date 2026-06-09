package com.ssambbong.gymjjak.organization.organization.domain.repository;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;

import java.util.Optional;

public interface OrganizationRepository {

    Long save(Organization organization);

    Optional<Organization> findById(Long organizationId);

    Optional<Organization> findByOrganizationAccountId(Long organizationAccountId);

    long countByStatus(OrganizationStatus status);
}
