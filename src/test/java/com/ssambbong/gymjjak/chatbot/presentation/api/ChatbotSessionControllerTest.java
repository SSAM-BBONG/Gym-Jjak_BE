package com.ssambbong.gymjjak.chatbot.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryItem;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotMessageQueryUseCase;
import com.ssambbong.gymjjak.chatbot.application.usecase.ChatbotSessionQueryUseCase;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;
import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.infrastructure.config.AiServiceProperties;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatbotSessionController.class)
class ChatbotSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatbotSessionQueryUseCase queryUseCase;

    @MockitoBean
    private ChatbotMessageQueryUseCase messageQueryUseCase;

    @MockitoBean
    private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockitoBean
    private AiServiceProperties aiServiceProperties;

    @Test
    void findSessions_returnsSessionListForAuthenticatedUser() throws Exception {
        when(queryUseCase.findSessions(any())).thenReturn(new ChatbotSessionListResult(
                List.of(new ChatbotSessionListItem(
                        "session-uuid", "첫 질문", "마지막 답변", LocalDateTime.of(2026, 7, 23, 10, 30)
                )),
                "next-cursor",
                true
        ));

        mockMvc.perform(get("/api/chatbot/sessions").with(authentication(userAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("CHATBOT_SESSION_LIST_SUCCESS"))
                .andExpect(jsonPath("$.data.sessions[0].sessionId").value("session-uuid"))
                .andExpect(jsonPath("$.data.sessions[0].title").value("첫 질문"))
                .andExpect(jsonPath("$.data.sessions[0].lastMessage").value("마지막 답변"))
                .andExpect(jsonPath("$.data.nextCursor").value("next-cursor"))
                .andExpect(jsonPath("$.data.hasNext").value(true));

        verify(queryUseCase).findSessions(new FindChatbotSessionsQuery(7L, null, 20));
    }

    @Test
    void findSessions_returnsBadRequestWhenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/chatbot/sessions")
                        .param("size", "51")
                        .with(authentication(userAuthentication())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400"));

        verifyNoInteractions(queryUseCase);
    }

    @Test
    void findSessions_usesDefaultSizeWhenSizeIsOmitted() throws Exception {
        when(queryUseCase.findSessions(any())).thenReturn(new ChatbotSessionListResult(List.of(), null, false));

        mockMvc.perform(get("/api/chatbot/sessions").with(authentication(userAuthentication())))
                .andExpect(status().isOk());

        verify(queryUseCase).findSessions(new FindChatbotSessionsQuery(7L, null, 20));
    }

    @Test
    void findMessages_returnsChronologicalHistoryForAuthenticatedUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        when(messageQueryUseCase.findMessages(any())).thenReturn(new ChatbotMessageHistoryResult(
                List.of(
                        new ChatbotMessageHistoryItem(
                                10L, ChatbotMessageRole.USER, "첫 질문", null, null,
                                null, objectMapper.readTree("[]"), null,
                                LocalDateTime.of(2026, 7, 23, 10, 0)
                        ),
                        new ChatbotMessageHistoryItem(
                                11L, ChatbotMessageRole.ASSISTANT, "답변", "ROUTINE", "ROUTINE",
                                objectMapper.readTree("{\"name\":\"상체 운동\"}"),
                                objectMapper.readTree("[{\"title\":\"출처\"}]"), false,
                                LocalDateTime.of(2026, 7, 23, 10, 1)
                        )
                ),
                "previous-cursor",
                true
        ));

        mockMvc.perform(get("/api/chatbot/sessions/session-1/messages")
                        .with(authentication(userAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("CHATBOT_MESSAGE_HISTORY_SUCCESS"))
                .andExpect(jsonPath("$.data.messages[0].messageId").value(10))
                .andExpect(jsonPath("$.data.messages[0].role").value("USER"))
                .andExpect(jsonPath("$.data.messages[1].routine.name").value("상체 운동"))
                .andExpect(jsonPath("$.data.messages[1].sources[0].title").value("출처"))
                .andExpect(jsonPath("$.data.nextCursor").value("previous-cursor"))
                .andExpect(jsonPath("$.data.hasNext").value(true));

        verify(messageQueryUseCase).findMessages(new FindChatbotMessagesQuery(7L, "session-1", null, 20));
    }

    @Test
    void findMessages_returnsBadRequestWhenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/chatbot/sessions/session-1/messages")
                        .param("size", "51")
                        .with(authentication(userAuthentication())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400"));

        verifyNoInteractions(messageQueryUseCase);
    }

    @Test
    void findMessages_usesDefaultSizeWhenSizeIsOmitted() throws Exception {
        when(messageQueryUseCase.findMessages(any())).thenReturn(new ChatbotMessageHistoryResult(List.of(), null, false));

        mockMvc.perform(get("/api/chatbot/sessions/session-1/messages")
                        .with(authentication(userAuthentication())))
                .andExpect(status().isOk());

        verify(messageQueryUseCase).findMessages(new FindChatbotMessagesQuery(7L, "session-1", null, 20));
    }

    private Authentication userAuthentication() {
        AuthUser user = new AuthUser(7L, "member@example.com", "USER");
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }
}
