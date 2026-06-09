package com.ssambbong.gymjjak.organization.domain.repository;

import com.ssambbong.gymjjak.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationStatus;

import java.util.Optional;

public interface OrganizationRepository {

    Long save(Organization organization);

    Optional<Organization> findById(Long organizationId);

    long countByStatus(OrganizationStatus status);
}
