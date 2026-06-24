package com.ssambbong.gymjjak.organization.organization.application.query;

import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.AdminTrainerSummary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrganizationAdminDetailResult(
        Long organizationId,
        String requestedLoginId,
        Long businessLicenseFileId,
        String businessRegistrationNumber,
        String businessName,
        String representativeName,
        String representativePhone,
        LocalDate openingDate,
        String roadAddress,
        String detailAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String websiteUrl,
        String instagramUrl,
        String blogUrl,
        String facilityPhone,
        OrganizationStatus status,
        LocalDateTime approvedAt,
        int trainerCount,
        List<AdminTrainerSummary> trainers
) {}
