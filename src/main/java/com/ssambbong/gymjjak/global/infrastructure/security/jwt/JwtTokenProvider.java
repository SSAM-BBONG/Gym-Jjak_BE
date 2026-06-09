package com.ssambbong.gymjjak.global.infrastructure.security.jwt;

import com.ssambbong.gymjjak.global.domain.auth.AuthErrorCode;
import com.ssambbong.gymjjak.global.domain.auth.AuthException;
import com.ssambbong.gymjjak.global.domain.auth.JwtClaims;
import com.ssambbong.gymjjak.global.domain.auth.RefreshTokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createAccessToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public RefreshTokenClaims parseRefreshToken(String token) {
        Claims claims = parseClaims(token);

        Long userId = parseSubjectAsUserId(claims);
        String username = claims.get("username", String.class);

        if (username == null || username.isBlank()) {
            throw new AuthException(AuthErrorCode.USERNAME_CLAIM_MISSING);
        }

        return new RefreshTokenClaims(userId, username);
    }

    public JwtClaims parseAccessToken(String token) {
        Claims claims = parseClaims(token);

        Long userId = parseSubjectAsUserId(claims);
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);

        if (username == null || username.isBlank()) {
            throw new AuthException(AuthErrorCode.USERNAME_CLAIM_MISSING);
        }

        if (role == null || role.isBlank()) {
            throw new AuthException(AuthErrorCode.ROLE_CLAIM_MISSING);
        }

        return new JwtClaims(userId, username, role);
    }

    public boolean isTokenExpired(String token) {
        try {
            parseClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Long parseSubjectAsUserId(Claims claims) {
        String subject = claims.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }

        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SUBJECT);
        }
    }

}
