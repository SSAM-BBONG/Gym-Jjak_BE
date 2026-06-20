package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "소셜회원가입 추가 정보 입력")
public record CompleteSocialSignupRequest(
        @Schema(description = "닉네임", example = "왕구리")
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @Schema(description = "핸드폰", example = "010-2222-3333")
        @NotBlank(message = "전화번호는 필수입니다.")
        String phone
) {
}
