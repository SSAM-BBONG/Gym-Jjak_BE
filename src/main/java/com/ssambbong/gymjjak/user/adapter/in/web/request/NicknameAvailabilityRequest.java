package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record NicknameAvailabilityRequest(
        @Schema(description = "중복 확인할 닉네임", example = "짐짝이")
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname
) {
}
