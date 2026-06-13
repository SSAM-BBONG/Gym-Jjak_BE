package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response;

import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;

public record FindAllOrganizationApplicationsResponse(
        Long organizationApplicationId,
        String requestedLoginId,
        String businessName,
        String representativeName,
        String representativePhone
) {
    public static FindAllOrganizationApplicationsResponse from(OrganizationApplication domain) {
        return new FindAllOrganizationApplicationsResponse(
                domain.getOrganizationApplicationId(),
                domain.getRequestedLoginId(),
                domain.getBusinessName(),
                domain.getRepresentativeName(),
                domain.getRepresentativePhone()
        );
    }
}
