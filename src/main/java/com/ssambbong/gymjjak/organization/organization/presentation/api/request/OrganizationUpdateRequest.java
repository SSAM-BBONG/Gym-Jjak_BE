package com.ssambbong.gymjjak.organization.organization.presentation.api.request;

import jakarta.validation.constraints.Pattern;

public record OrganizationUpdateRequest(
        @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 02-1234-5678)")
        String facilityPhone,
        String instagramUrl,
        String blogUrl,
        String websiteUrl
) {
}
