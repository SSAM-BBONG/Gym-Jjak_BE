package com.ssambbong.gymjjak.organization.domain.repository;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.Status;

public interface OrganizationApplicationRepository {

    boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber);

    Long save(OrganizationApplication organizationApplication);

}
