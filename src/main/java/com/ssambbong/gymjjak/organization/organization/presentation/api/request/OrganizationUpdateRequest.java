package com.ssambbong.gymjjak.organization.organization.presentation.api.request;

import jakarta.validation.constraints.Pattern;

public record OrganizationUpdateRequest(
        @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 02-1234-5678)")
        String facilityPhone,

        @Pattern(regexp = "^https?://\\S+$", message = "올바른 URL 형식이 아닙니다. (예: https://instagram.com/...)")
        String instagramUrl,

        @Pattern(regexp = "^https?://\\S+$", message = "올바른 URL 형식이 아닙니다. (예: https://blog.naver.com/...)")
        String blogUrl,

        @Pattern(regexp = "^https?://\\S+$", message = "올바른 URL 형식이 아닙니다. (예: https://example.com)")
        String websiteUrl
) {
}
