package com.ssambbong.gymjjak.global.application.auth.port.out;

import com.ssambbong.gymjjak.global.domain.auth.JwtClaims;

public interface JwtTokenPort {
    boolean validateToken(String token);

    boolean isTokenExpired(String token);

    JwtClaims parseAccessToken(String token);
}
