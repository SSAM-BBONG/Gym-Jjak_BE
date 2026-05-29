package com.ssambbong.gymjjak.organization.application.usecase;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;

import java.util.List;

public interface OrganizationApplicationQueryUsecase {

    List<OrganizationApplication> findMyOrganizationApplications(Long applicantUserId);

    OrganizationApplication findOrganizationApplicationDetails(Long organizationApplicationId, Long requestUserId, boolean isAdmin);

    List<OrganizationApplication> findPendingOrganizationApplications();
}
