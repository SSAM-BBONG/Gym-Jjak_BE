package com.ssambbong.gymjjak.user.application.command;

import com.ssambbong.gymjjak.user.domain.model.UserStatus;

public record UpdateUserStatusCommand(
        Long userId,
        Long adminId,
        UserStatus status,
        String reason
) {
}
