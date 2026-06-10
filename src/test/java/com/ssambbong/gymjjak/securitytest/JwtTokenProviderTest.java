package com.ssambbong.gymjjak.securitytest;

import com.ssambbong.gymjjak.global.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createAccessToken_and_validate() {
        String token = jwtTokenProvider.createAccessToken(
                1L,
                "user@test.com",
                "USER"
        );

        boolean result = jwtTokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    void parseToken_and_extract_role() {
        String token = jwtTokenProvider.createAccessToken(
                1L,
                "user@test.com",
                "ADMIN"
        );

        String role = jwtTokenProvider.parseAccessToken(token).role();

        assertThat(role).isEqualTo("ADMIN");
    }
}
