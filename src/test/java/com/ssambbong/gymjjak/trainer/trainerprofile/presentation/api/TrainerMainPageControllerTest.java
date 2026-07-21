package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api;

import com.ssambbong.gymjjak.global.infrastructure.security.config.SecurityConfig;
import com.ssambbong.gymjjak.global.infrastructure.security.oauth.OAuth2SuccessHandler;
import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import com.ssambbong.gymjjak.global.presentation.security.handler.CustomAccessDeniedHandler;
import com.ssambbong.gymjjak.global.presentation.security.handler.CustomAuthenticationEntryPoint;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerMainPageResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerMainPageQueryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerMainPageController.class)
@Import({
        SecurityConfig.class,
        CustomAccessDeniedHandler.class,
        CustomAuthenticationEntryPoint.class
})
class TrainerMainPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerMainPageQueryUseCase trainerMainPageQueryUseCase;

    @MockitoBean
    private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Test
    void findMainPage_returnsDashboardForTrainer() throws Exception {
        // TRAINER 권한 사용자는 자신의 대시보드 카드와 집계 값을 조회합니다.
        when(trainerMainPageQueryUseCase.findMainPage(7L))
                .thenReturn(new TrainerMainPageResult(
                        2L,
                        12L,
                        new BigDecimal("4.8"),
                        15,
                        List.of(new TrainerMainPageResult.InProgressPtCourse(
                                101L,
                                "https://example.com/thumbnail.jpg",
                                "체형 교정 PT",
                                "홍길동",
                                "짐잭 강남점",
                                70000,
                                5L
                        ))
                ));

        mockMvc.perform(
                        get("/api/dashboard/trainer/main")
                                .with(authentication(authenticationOf(7L, "TRAINER")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("TRAINER_PROFILE_200_5"))
                .andExpect(jsonPath("$.data.organizationCount").value(2))
                .andExpect(jsonPath("$.data.currentStudentCount").value(12))
                .andExpect(jsonPath("$.data.inProgressPtCourses[0].organizationName").value("짐잭 강남점"))
                .andExpect(jsonPath("$.data.inProgressPtCourses[0].currentStudentCount").value(5));

        verify(trainerMainPageQueryUseCase).findMainPage(7L);
    }

    @Test
    void findMainPage_rejectsNonTrainer() throws Exception {
        mockMvc.perform(
                        get("/api/dashboard/trainer/main")
                                .with(authentication(authenticationOf(8L, "USER")))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(trainerMainPageQueryUseCase);
    }

    @Test
    void findMainPage_rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/dashboard/trainer/main"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(trainerMainPageQueryUseCase);
    }

    private Authentication authenticationOf(Long userId, String role) {
        return new UsernamePasswordAuthenticationToken(
                new AuthUser(userId, "trainer@test.com", role),
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
