package com.ssambbong.gymjjak.user.application.command;

public record CompleteSocialSignupCommand(
        Long userId,
        String nickname,
        String phone
) {
}
