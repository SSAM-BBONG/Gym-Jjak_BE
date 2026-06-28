package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FindMyOrganizationResponse(

        // 기본 정보 (수정 불가)
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
        String businessLicenseFileUrl,
        String businessLicenseOriginalName,

        // 추가 정보 (수정 가능)
        String facilityPhone,
        String instagramUrl,
        String blogUrl,
        String websiteUrl
) {
}
