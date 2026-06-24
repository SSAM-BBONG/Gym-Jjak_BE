package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record FindOrganizationAdminDetailResponse(
        Long organizationId,
        String requestedLoginId,
        String businessLicenseFileUrl,
        String businessLicenseOriginalName,
        String businessRegistrationNumber,
        String businessName,
        String representativeName,
        String representativePhone,
        LocalDate openingDate,
        String roadAddress,
        String detailAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String facilityPhone,
        String instagramUrl,
        String blogUrl,
        String websiteUrl,
        OrganizationStatus status,
        LocalDateTime approvedAt,
        int trainerCount,
        List<TrainerInfo> trainers
) {
    public record TrainerInfo(
            String trainerName,
            String email,
            LocalDateTime registeredAt
    ) {}
}
