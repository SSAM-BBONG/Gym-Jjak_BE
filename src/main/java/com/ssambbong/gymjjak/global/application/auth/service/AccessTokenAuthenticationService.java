package com.ssambbong.gymjjak.global.application.auth.service;

import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.application.auth.port.out.JwtTokenPort;
import com.ssambbong.gymjjak.global.domain.auth.AuthErrorCode;
import com.ssambbong.gymjjak.global.domain.auth.AuthException;
import com.ssambbong.gymjjak.global.domain.auth.JwtClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessTokenAuthenticationService implements AuthenticateAccessTokenUseCase {

    private final JwtTokenPort jwtTokenPort;

    @Override
    public JwtClaims authenticate(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new AuthException(AuthErrorCode.ACCESS_TOKEN_MISSING);
        }

        if (jwtTokenPort.isTokenExpired(accessToken)) {
            throw new AuthException(AuthErrorCode.ACCESS_TOKEN_EXPIRED);
        }

        if (!jwtTokenPort.validateToken(accessToken)) {
            throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        JwtClaims claims = jwtTokenPort.parseAccessToken(accessToken);
        validateClaims(claims);

        return claims;
    }

    private void validateClaims(JwtClaims claims) {
        if (claims.userId() == null) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }

        if (claims.username() == null || claims.username().isBlank()) {
            throw new AuthException(AuthErrorCode.USERNAME_CLAIM_MISSING);
        }

        if (claims.role() == null || claims.role().isBlank()) {
            throw new AuthException(AuthErrorCode.ROLE_CLAIM_MISSING);
        }
    }
}
