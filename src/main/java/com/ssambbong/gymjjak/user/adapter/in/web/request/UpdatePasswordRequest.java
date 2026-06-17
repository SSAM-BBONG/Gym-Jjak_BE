package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordRequest(

        @Schema(description = "새 비밀번호", example = "Test12345!")
        @NotBlank(message = "새 비밀번호를 입력하세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 8자 이상 16자 이하이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        String newPassword,

        @Schema(description = "비밀번호 확인", example = "Test12345!")
        @NotBlank(message = "비밀번호를 다시 확인하세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 8자 이상 16자 이하이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        String checkNewPassword
) {
}
