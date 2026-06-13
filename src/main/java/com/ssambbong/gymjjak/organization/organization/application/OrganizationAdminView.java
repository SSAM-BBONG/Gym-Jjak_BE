package com.ssambbong.gymjjak.organization.organization.application;

import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;

import java.time.LocalDateTime;

public record OrganizationAdminView(
        Long organizationId,
        String loginId,
        String businessName,
        String representativeName,
        String representativePhone,
        long trainerCount,
        OrganizationStatus status,
        LocalDateTime createdAt
) {
}
