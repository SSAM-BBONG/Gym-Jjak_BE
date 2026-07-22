package com.ssambbong.gymjjak.user.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 조회/수정 응답")
public record UserProfileResponse(
        @Schema(description = "본명")
        String name,
        @Schema(description = "닉네임")
        String nickname,
        @Schema(description = "전화번호")
        String phone,
        @Schema(description = "현재 유료 구독 이용 여부")
        boolean paid

) {
}
