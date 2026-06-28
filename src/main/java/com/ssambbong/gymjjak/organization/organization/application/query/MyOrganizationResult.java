package com.ssambbong.gymjjak.organization.organization.application.query;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MyOrganizationResult(
        Long businessLicenseFileId,
        String requestedLoginId,
        String businessRegistrationNumber,
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
        String websiteUrl
) {
}
