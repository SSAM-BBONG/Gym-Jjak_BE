package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PhoneAvailabilityRequest(
        @Schema(description = "중복 확인할 전화번호", example = "010-1234-5678")
        @NotBlank(message = "전화번호는 필수입니다.")
        String phone
) {
}
