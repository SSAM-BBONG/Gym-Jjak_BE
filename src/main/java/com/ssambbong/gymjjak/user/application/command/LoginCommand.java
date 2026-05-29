package com.ssambbong.gymjjak.user.application.command;

public record LoginCommand(
        String username,
        String password
) {
}
