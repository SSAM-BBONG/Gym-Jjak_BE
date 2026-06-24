package com.ssambbong.gymjjak.organization.organizationApplication.application.usecase;

import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListQuery;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;

import java.util.List;

public interface OrganizationApplicationQueryUsecase {

    List<OrganizationApplication> findMyOrganizationApplications(Long applicantUserId);

    OrganizationApplication findOrganizationApplicationDetails(Long organizationApplicationId, Long requestUserId, boolean isAdmin);

    ApplicationListResult findPendingOrganizationApplications(ApplicationListQuery query);

    void checkLoginIdDuplicate(String requestedLoginId);
}
