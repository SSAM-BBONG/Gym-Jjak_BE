package com.ssambbong.gymjjak.organization.domain.repository;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;

import java.util.List;
import java.util.Optional;

public interface OrganizationApplicationRepository {

    boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber);

    boolean existsByRequestedLoginId(String requestedLoginId);

    Long save(OrganizationApplication organizationApplication);

    List<OrganizationApplication> findAllByApplicantUserId(Long applicantUserId);

    Optional<OrganizationApplication> findById(Long organizationApplicationId);

    List<OrganizationApplication> findAllByStatus(OrganizationApplicationStatus status);

    void approve(OrganizationApplication organizationApplication);

    void reject(OrganizationApplication organizationApplication);

    void cancel(OrganizationApplication organizationApplication);

    Optional<OrganizationApplication> findByIdAndApplicantUserId(Long organizationApplicationId, Long applicantId);

    long count();

    long countByStatus(OrganizationApplicationStatus status);
}
