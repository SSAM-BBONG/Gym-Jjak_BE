package com.ssambbong.gymjjak.organization.application.command;

import java.math.BigDecimal;

public record OrganizationApplicationCreateCommand(
        Long applicantUserId,
        String requestedLoginId,
        Long businessLicenseFileId,
        String businessRegistrationNumber,
        String businessName,
        String representativeName,
        String representativePhone,
        String openingDate,
        String roadAddress,
        String jibunAddress,
        String detailAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String websiteUrl,
        String instagramUrl,
        String blogUrl,
        String facilityPhone
) {
}
