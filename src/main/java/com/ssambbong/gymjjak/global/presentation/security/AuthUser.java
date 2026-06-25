package com.ssambbong.gymjjak.global.presentation.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public record AuthUser(
        Long userId,
        String username,
        String role
) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
