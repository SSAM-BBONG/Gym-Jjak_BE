package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PasswordVerificationRequest(
        @Schema(description = "비밀번호", example = "Test1234!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
