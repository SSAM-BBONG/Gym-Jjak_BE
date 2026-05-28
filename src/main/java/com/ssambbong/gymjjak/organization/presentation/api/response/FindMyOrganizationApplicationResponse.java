package com.ssambbong.gymjjak.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;

import java.time.LocalDateTime;

public record FindMyOrganizationApplicationResponse(
        Long organizationApplicationId,
        String businessName,
        String requestedLoginId,
        OrganizationApplicationStatus status,
        String businessRegistrationNumber,
        String representativeName,
        LocalDateTime createdAt
) {
}
