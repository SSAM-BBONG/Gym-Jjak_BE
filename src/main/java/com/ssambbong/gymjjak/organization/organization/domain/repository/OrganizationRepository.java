package com.ssambbong.gymjjak.organization.organization.domain.repository;

import com.ssambbong.gymjjak.organization.organization.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListQuery;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;

import java.util.Optional;

public interface OrganizationRepository {

    Long save(Organization organization);

    Optional<Organization> findById(Long organizationId);

    Optional<Organization> findByOrganizationAccountId(Long organizationAccountId);

    Optional<MyOrganizationResult> findMyOrganizationByAccountId(Long organizationAccountId);

    OrganizationListResult findAllForAdmin(OrganizationListQuery query);

    void update(Organization organization);

    long countByStatus(OrganizationStatus status);

    Optional<String> findRequestedLoginIdById(Long organizationId);
}
