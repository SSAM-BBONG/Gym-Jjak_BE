package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response;

public record FindAllOrganizationApplicationsResponse(
        Long organizationApplicationId,
        String requestedLoginId,
        String businessName,
        String representativeName,
        String representativePhone
) {
}
