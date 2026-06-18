package com.ssambbong.gymjjak.user.adapter.in.web.response;

import com.ssambbong.gymjjak.user.application.result.FindBlacklistUserResult;
import com.ssambbong.gymjjak.user.domain.model.BlacklistType;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "블랙리스트 회원 조회 응답")
public record FindBlacklistUserResponse(

        @Schema(description = "회원 ID")
        Long userId,

        @Schema(description = "이메일")
        String username,

        @Schema(description = "이름")
        String name,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "회원 상태")
        UserStatus status,

        @Schema(description = "블랙리스트 유형")
        BlacklistType blacklistType,

        @Schema(description = "관리자 처리 사유")
        String reason

) {

    public static FindBlacklistUserResponse from(FindBlacklistUserResult result) {
        return new FindBlacklistUserResponse(
                result.userId(),
                result.username(),
                result.name(),
                result.nickname(),
                result.status(),
                result.blacklistType(),
                result.reason()
        );
    }
}
