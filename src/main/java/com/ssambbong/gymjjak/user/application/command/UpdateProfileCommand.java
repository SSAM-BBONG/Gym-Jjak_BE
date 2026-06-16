package com.ssambbong.gymjjak.user.application.command;

public record UpdateProfileCommand(
        Long userId,
        String name,
        String nickname,
        String phone
) {
}
