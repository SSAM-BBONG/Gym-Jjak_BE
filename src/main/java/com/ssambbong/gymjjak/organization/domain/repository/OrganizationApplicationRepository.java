package com.ssambbong.gymjjak.organization.domain.repository;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;

import java.util.List;

public interface OrganizationApplicationRepository {

    boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber);

    Long save(OrganizationApplication organizationApplication);

    List<OrganizationApplication> findAllByApplicantUserId(Long applicantUserId);

}
