package com.ssambbong.gymjjak.user.application.command;

public record RegisterUserCommand(
        String username,
        String password,
        String name,
        String nickname,
        String phone
) {
}
