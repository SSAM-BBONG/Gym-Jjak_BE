package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(

        @Schema(description = "이메일", example = "test1234@test.com")
        @NotBlank(message = "아이디는 필수입니다.")
        String username,

        @Schema(description = "비밀번호", example = "Test1234!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
