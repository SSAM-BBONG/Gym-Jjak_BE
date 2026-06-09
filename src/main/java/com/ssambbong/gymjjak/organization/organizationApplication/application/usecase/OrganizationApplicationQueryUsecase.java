package com.ssambbong.gymjjak.organization.organizationApplication.application.usecase;

import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;

import java.util.List;

public interface OrganizationApplicationQueryUsecase {

    List<OrganizationApplication> findMyOrganizationApplications(Long applicantUserId);

    OrganizationApplication findOrganizationApplicationDetails(Long organizationApplicationId, Long requestUserId, boolean isAdmin);

    List<OrganizationApplication> findPendingOrganizationApplications();
}
