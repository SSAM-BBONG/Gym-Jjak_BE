package com.ssambbong.gymjjak.global.domain.auth;

public record RefreshTokenClaims(
        Long userId,
        String username
) {
}
