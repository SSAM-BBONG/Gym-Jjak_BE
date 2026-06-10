package com.ssambbong.gymjjak.user.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordVerificationRequest(
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
