package com.ssambbong.gymjjak.organization.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrganizationApplicationCreateCommand(
        Long applicantUserId,
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
        String facilityPhone
) {
}
