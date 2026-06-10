package com.ssambbong.gymjjak.global.presentation.security;

public record AuthUser(
        Long userId,
        String username,
        String role
) {
}
