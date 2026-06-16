package com.ssambbong.gymjjak.global.presentation.security;

import com.ssambbong.gymjjak.global.domain.auth.JwtClaims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthenticationConverter {

    public Authentication toAuthentication(JwtClaims claims) {
        AuthUser authUser = new AuthUser(
                claims.userId(),
                claims.username(),
                claims.role()
        );

        return new UsernamePasswordAuthenticationToken(
                authUser,
                null,
                List.of(new SimpleGrantedAuthority(claims.role()))
        );
    }
}
