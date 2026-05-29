package com.ssambbong.gymjjak.global.security.principal;

public record AuthUser(
        Long userId,
        String username,
        String role
) {
}
