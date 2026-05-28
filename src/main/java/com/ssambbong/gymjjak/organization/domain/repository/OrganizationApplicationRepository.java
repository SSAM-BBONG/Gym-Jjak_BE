package com.ssambbong.gymjjak.organization.domain.repository;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;

import java.util.List;
import java.util.Optional;

public interface OrganizationApplicationRepository {

    boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber);

    Long save(OrganizationApplication organizationApplication);

    List<OrganizationApplication> findAllByApplicantUserId(Long applicantUserId);

    Optional<OrganizationApplication> findById(Long organizationApplicationId);
}
