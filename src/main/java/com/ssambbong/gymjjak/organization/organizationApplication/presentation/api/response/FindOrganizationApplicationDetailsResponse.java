package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FindOrganizationApplicationDetailsResponse(
        Long organizationApplicationId,
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
        String websiteUrl,
        String instagramUrl,
        String blogUrl,
        String facilityPhone,
        String businessLicenseFileUrl,
        String businessLicenseOriginalName
) {
}
