package com.ssambbong.gymjjak.user.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
        @Schema(description = "엑세스 토큰")
        String accessToken,
        @Schema(description = "리프레시 토큰")
        String refreshToken,
        @Schema(description = "온보딩 유무")
        boolean onboardingCompleted

) {
}
