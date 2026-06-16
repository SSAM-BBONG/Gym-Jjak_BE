package com.ssambbong.gymjjak.user.application.command;

public record UpdatePasswordCommand(
        Long userId,
        String newPassword,
        String checkNewPassword
) {
}
