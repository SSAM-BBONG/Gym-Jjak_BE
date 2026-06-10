package com.ssambbong.gymjjak.global.infrastructure.security.jwt;

import com.ssambbong.gymjjak.global.application.auth.port.out.JwtTokenPort;
import com.ssambbong.gymjjak.global.domain.auth.JwtClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements JwtTokenPort {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return jwtTokenProvider.isTokenExpired(token);
    }

    @Override
    public JwtClaims parseAccessToken(String token) {
        return jwtTokenProvider.parseAccessToken(token);
    }
}
