package com.ssambbong.gymjjak.user.adapter.in.web.response;

import com.ssambbong.gymjjak.user.application.result.FindTrainerUserResult;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;

public record FindTrainerUserResponse(
        String username,
        String name,
        String nickname,
        UserStatus status
) {

    public static FindTrainerUserResponse from(FindTrainerUserResult result) {
        return new FindTrainerUserResponse(
                result.username(),
                result.name(),
                result.nickname(),
                result.status()
        );
    }
}
