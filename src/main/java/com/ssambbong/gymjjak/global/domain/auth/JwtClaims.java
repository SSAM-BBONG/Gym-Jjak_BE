package com.ssambbong.gymjjak.global.domain.auth;

public record JwtClaims(
        Long userId,
        String username,
        String role
) {

}
