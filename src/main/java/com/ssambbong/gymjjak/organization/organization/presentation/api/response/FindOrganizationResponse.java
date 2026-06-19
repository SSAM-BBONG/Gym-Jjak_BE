package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FindOrganizationResponse(
        Long organizationId,
        String businessRegistrationNumber,
        Long businessLicenseFileId,
        String businessName,
        String representativeName,
        String representativePhone,
        LocalDate openingDate,
        String roadAddress,
        String jibunAddress,
        String detailAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String facilityPhone,
        String instagramUrl,
        String blogUrl,
        String websiteUrl,
        OrganizationStatus status,
        LocalDateTime createdAt
) {}
