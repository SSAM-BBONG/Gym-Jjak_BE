package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;

import java.time.LocalDateTime;

public record FindOrganizationsResponse(
        Long organizationId,
        String loginId,
        String businessName,
        String representativeName,
        String representativePhone,
        long trainerCount,
        OrganizationStatus status,
        LocalDateTime createdAt
) {}
