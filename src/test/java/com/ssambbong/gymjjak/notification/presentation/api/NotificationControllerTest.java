package com.ssambbong.gymjjak.notification.presentation.api;

import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import com.ssambbong.gymjjak.notification.application.result.UnreadNotificationCountResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationQueryUseCase;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationUserCommandUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationQueryUseCase queryUseCase;

    @MockitoBean
    private NotificationUserCommandUseCase userCommandUseCase;

    @MockitoBean
    private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Test
    void findUnreadNotificationCount_returnsCountForAuthenticatedUser() throws Exception {
        // 인증된 사용자의 미읽음 알림 수를 헤더용 응답으로 반환합니다.
        AuthUser user = new AuthUser(7L, "user@test.com", "USER");
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        when(queryUseCase.findUnreadNotificationCount(7L))
                .thenReturn(new UnreadNotificationCountResult(3L));

        mockMvc.perform(
                        get("/api/notifications/unread-count")
                                .with(authentication(userAuthentication))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("NOTIFICATION_200_5"))
                .andExpect(jsonPath("$.data.unreadCount").value(3));

        verify(queryUseCase).findUnreadNotificationCount(7L);
    }
}
