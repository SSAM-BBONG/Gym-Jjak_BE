package com.ssambbong.gymjjak.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chat.application.usecase.ChatRoomUseCase;
import com.ssambbong.gymjjak.chat.exception.*;
import com.ssambbong.gymjjak.chat.presentation.api.ChatRoomController;
import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatRoomUseCase chatRoomUseCase;

    @MockitoBean
    private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private Authentication userAuth;
    private Authentication trainerAuth;

    @BeforeEach
    void setUp() {
        userAuth = new UsernamePasswordAuthenticationToken(
                new AuthUser(1L, "test1234", "USER"), null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        trainerAuth = new UsernamePasswordAuthenticationToken(
                new AuthUser(11L, "trainer1", "TRAINER"), null,
                List.of(new SimpleGrantedAuthority("TRAINER"))
        );
    }

    @Nested
    @DisplayName("POST /api/chat/rooms - 채팅방 생성")
    class CreateChatRoom {

        private String requestBody(Long trainerProfileId, Long ptCourseId) throws Exception {
            return objectMapper.writeValueAsString(Map.of("trainerProfileId", trainerProfileId, "ptCourseId", ptCourseId));
        }

        @Test
        @DisplayName("정상 생성 시 201을 반환한다")
        void success_201() throws Exception {
            when(chatRoomUseCase.createChatRoom(any())).thenReturn(1L);

            mockMvc.perform(post("/api/chat/rooms")
                            .with(authentication(userAuth))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody(11L, 1L)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("존재하지 않는 트레이너이면 404를 반환한다")
        void trainerNotFound_404() throws Exception {
            when(chatRoomUseCase.createChatRoom(any())).thenThrow(new TrainerNotFoundException());

            mockMvc.perform(post("/api/chat/rooms")
                            .with(authentication(userAuth))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody(999L, 1L)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("CHAT_006"));
        }

        @Test
        @DisplayName("이미 ACTIVE 채팅방이 존재하면 409를 반환한다")
        void alreadyExists_409() throws Exception {
            when(chatRoomUseCase.createChatRoom(any())).thenThrow(new ChatRoomAlreadyExistsException());

            mockMvc.perform(post("/api/chat/rooms")
                            .with(authentication(userAuth))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody(11L, 1L)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("CHAT_002"));
        }

        @Test
        @DisplayName("유효하지 않은 PT 코스이면 400을 반환한다")
        void invalidPtCourse_400() throws Exception {
            when(chatRoomUseCase.createChatRoom(any())).thenThrow(new PtCourseNotFoundException());

            mockMvc.perform(post("/api/chat/rooms")
                            .with(authentication(userAuth))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody(11L, 999L)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CHAT_007"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/chat/rooms/{chatRoomId}/leave - 채팅방 나가기")
    class LeaveChatRoom {

        @Test
        @DisplayName("존재하지 않는 채팅방이면 404를 반환한다")
        void chatRoomNotFound_404() throws Exception {
            doThrow(new ChatRoomNotFoundException()).when(chatRoomUseCase).leaveChatRoom(anyLong(), anyLong());

            mockMvc.perform(patch("/api/chat/rooms/999/leave")
                            .with(authentication(userAuth))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("CHAT_001"));
        }

        @Test
        @DisplayName("참여자가 아닌 사용자가 나가려 하면 403을 반환한다")
        void accessDenied_403() throws Exception {
            doThrow(new ChatRoomAccessDeniedException()).when(chatRoomUseCase).leaveChatRoom(anyLong(), anyLong());

            mockMvc.perform(patch("/api/chat/rooms/1/leave")
                            .with(authentication(userAuth))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("CHAT_003"));
        }

        @Test
        @DisplayName("이미 종료된 채팅방이면 400을 반환한다")
        void alreadyClosed_400() throws Exception {
            doThrow(new ChatRoomClosedException()).when(chatRoomUseCase).leaveChatRoom(anyLong(), anyLong());

            mockMvc.perform(patch("/api/chat/rooms/1/leave")
                            .with(authentication(userAuth))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CHAT_005"));
        }

        @Test
        @DisplayName("이미 나간 채팅방이면 409를 반환한다")
        void alreadyLeft_409() throws Exception {
            doThrow(new ChatRoomAlreadyLeftException()).when(chatRoomUseCase).leaveChatRoom(anyLong(), anyLong());

            mockMvc.perform(patch("/api/chat/rooms/1/leave")
                            .with(authentication(userAuth))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("CHAT_004"));
        }
    }
}
