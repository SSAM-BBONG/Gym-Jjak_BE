package com.ssambbong.gymjjak.user.application.result;

import com.ssambbong.gymjjak.user.domain.model.UserStatus;

public record FindTrainerUserResult(
        String username,
        String name,
        String nickname,
        UserStatus status
) {
}
