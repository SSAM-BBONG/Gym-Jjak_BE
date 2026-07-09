package com.ssambbong.gymjjak.user.adapter.in.web.response;

import com.ssambbong.gymjjak.user.application.result.UserUsernameAndNicknameResult;

public record UserUsernameAndNicknameResponse(
        String username,
        String nickname,
        boolean socialUser
) {

    public static UserUsernameAndNicknameResponse from(
            UserUsernameAndNicknameResult result
    ) {
        return new UserUsernameAndNicknameResponse(
                result.username(),
                result.nickname(),
                result.socialUser()
        );
    }
}
