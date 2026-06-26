package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "비밀번호 찾기 요청")
public record FindPasswordRequest(

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(description = "이메일", example = "test1234@test.com")
        @NotBlank(message = "아이디는 필수입니다.")
        String username
) {
}
