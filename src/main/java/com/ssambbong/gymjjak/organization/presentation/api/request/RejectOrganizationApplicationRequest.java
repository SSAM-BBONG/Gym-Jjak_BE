package com.ssambbong.gymjjak.organization.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record RejectOrganizationApplicationRequest(
        @NotBlank(message = "반려 사유는 필수입니다.")
        String rejectReason
) {
}
