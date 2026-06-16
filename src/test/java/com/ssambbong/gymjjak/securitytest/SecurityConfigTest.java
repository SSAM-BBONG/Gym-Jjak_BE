package com.ssambbong.gymjjak.securitytest;

import com.ssambbong.gymjjak.global.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfigTest.SecurityTestController.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("토큰 없이 /api/users/test 접근하면 401 Unauthorized")
    void usersApi_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/users/test"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("USER 토큰으로 /api/users/test 접근하면 200 OK")
    void usersApi_withUserToken_returns200() throws Exception {
        String token = jwtTokenProvider.createAccessToken(
                1L,
                "user@test.com",
                "USER"
        );

        mockMvc.perform(get("/api/users/test")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER 토큰으로 /api/admin/test 접근하면 403 Forbidden")
    void adminApi_withUserToken_returns403() throws Exception {
        String token = jwtTokenProvider.createAccessToken(
                1L,
                "user@test.com",
                "USER"
        );

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN 토큰으로 /api/admin/test 접근하면 200 OK")
    void adminApi_withAdminToken_returns200() throws Exception {
        String token = jwtTokenProvider.createAccessToken(
                2L,
                "admin@test.com",
                "ADMIN"
        );

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @RestController
    static class SecurityTestController {

        @GetMapping("/api/users/test")
        public String userTest() {
            return "user access";
        }

        @GetMapping("/api/admin/test")
        public String adminTest() {
            return "admin access";
        }
    }
}
