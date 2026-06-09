package com.ssambbong.gymjjak.global.application.auth.port.in;

import com.ssambbong.gymjjak.global.domain.auth.JwtClaims;

public interface AuthenticateAccessTokenUseCase {
    JwtClaims authenticate(String accessToken);
}
