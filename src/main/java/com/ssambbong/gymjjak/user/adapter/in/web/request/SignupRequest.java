package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "회원 가입 요청")
public record SignupRequest(
        @Schema(description = "이메일", example = "test1234@test.com")
        @NotBlank(message = "이메일은 필수입니다.")
        String username,

        @Schema(description = "비밀번호", example = "Test1234!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 8자 이상 16자 이하이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        String password,

        @Schema(description = "이름", example = "서주원")
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @Schema(description = "닉네임", example = "섹시킹")
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @Schema(description = "전화번호", example = "010-1111-2222")
        @NotBlank(message = "전화번호는 필수입니다.")
        String phone


) {
}
