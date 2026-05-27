package com.ssambbong.gymjjak.securitytest;

import com.ssambbong.gymjjak.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void accessToken_생성_후_검증할_수_있다() {
        String token = jwtTokenProvider.createAccessToken(
                1L,
                "user@test.com",
                "USER"
        );

        boolean result = jwtTokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    void 토큰에서_role을_꺼낼_수_있다() {
        String token = jwtTokenProvider.createAccessToken(
                1L,
                "user@test.com",
                "ADMIN"
        );

        String role = jwtTokenProvider.getRole(token);

        assertThat(role).isEqualTo("ADMIN");
    }
}
