package com.ssambbong.gymjjak.user.adapter.in.web.response;

import com.ssambbong.gymjjak.user.application.result.FindUserResult;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 조회 응답")
public record FindUserResponse(

        @Schema(description = "유저 ID")
        Long userId,
        @Schema(description = "이메일")
        String username,
        @Schema(description = "이름")
        String name,
        @Schema(description = "닉네임")
        String nickname,
        @Schema(description = "상태")
        UserStatus status


) {
    public static FindUserResponse from(FindUserResult result) {
        return new FindUserResponse(
                result.userId(),
                result.username(),
                result.name(),
                result.nickname(),
                result.status()
        );
    }
}
