package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원 가입 요청")
public record SignupRequest(
        @Schema(description = "이메일", example = "test1234@test.com")
        @NotBlank String username,

        @Schema(description = "비밀번호", example = "Test1234!")
        @NotBlank String password,

        @Schema(description = "이름", example = "서주원")
        @NotBlank String name,

        @Schema(description = "닉네임", example = "섹시킹")
        @NotBlank String nickname,

        @Schema(description = "전화번호", example = "010-1111-2222")
        @NotBlank String phone


) {
}
