package com.ssambbong.gymjjak.chat;

import com.ssambbong.gymjjak.chat.application.command.SendChatMessageCommand;
import com.ssambbong.gymjjak.chat.application.port.ChatMetricsPort;
import com.ssambbong.gymjjak.chat.application.usecase.ChatMessageUseCase;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;
import com.ssambbong.gymjjak.chat.exception.ChatRoomNotFoundException;
import com.ssambbong.gymjjak.chat.presentation.websocket.ChatWebSocketController;
import com.ssambbong.gymjjak.chat.presentation.websocket.request.SendChatMessageRequest;
import com.ssambbong.gymjjak.chat.presentation.websocket.response.ChatErrorResponse;
import com.ssambbong.gymjjak.chat.presentation.websocket.response.ChatMessageBroadcast;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTest {

    @Mock private ChatMessageUseCase chatMessageUseCase;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private SimpUserRegistry simpUserRegistry;
    @Mock private ChatMetricsPort chatMetricsPort;

    @InjectMocks
    private ChatWebSocketController controller;

    private final AuthUser authUser = new AuthUser(1L, "test1234", "USER");

    private ChatMessage savedMessage() {
        return ChatMessage.restore(99L, 10L, 1L, "안녕하세요", false, LocalDateTime.of(2024, 1, 1, 12, 0));
    }

    // 웹소켓 핸들러가 실제로 받는 Principal 형태로 인증 주체를 전달합니다.
    private final Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null);

    @Nested
    @DisplayName("메시지 전송 - /app/chat.send")
    class SendMessage {

        @Test
        @DisplayName("저장된 메시지를 올바른 destination으로 브로드캐스트한다")
        void broadcastsToCorrectDestination() {
            when(chatMessageUseCase.createMessage(any())).thenReturn(savedMessage());
            when(simpUserRegistry.findSubscriptions(any())).thenReturn(Set.of());

            controller.sendMessage(new SendChatMessageRequest(10L, "안녕하세요"), authentication);

            verify(messagingTemplate).convertAndSend(eq("/topic/chat.room.10"), any(ChatMessageBroadcast.class));
        }

        @Test
        @DisplayName("요청의 chatRoomId와 authUser의 userId로 커맨드를 구성하여 저장한다")
        void buildsCommandFromRequestAndAuthUser() {
            when(chatMessageUseCase.createMessage(any())).thenReturn(savedMessage());
            when(simpUserRegistry.findSubscriptions(any())).thenReturn(Set.of());

            controller.sendMessage(new SendChatMessageRequest(10L, "안녕하세요"), authentication);

            ArgumentCaptor<SendChatMessageCommand> captor = ArgumentCaptor.forClass(SendChatMessageCommand.class);
            verify(chatMessageUseCase).createMessage(captor.capture());
            SendChatMessageCommand command = captor.getValue();
            assertThat(command.chatRoomId()).isEqualTo(10L);
            assertThat(command.senderId()).isEqualTo(1L);
            assertThat(command.content()).isEqualTo("안녕하세요");
        }

        @Test
        @DisplayName("브로드캐스트 페이로드에 저장된 메시지 정보가 담긴다")
        void broadcastPayloadMatchesSavedMessage() {
            ChatMessage message = savedMessage();
            when(chatMessageUseCase.createMessage(any())).thenReturn(message);
            when(simpUserRegistry.findSubscriptions(any())).thenReturn(Set.of());

            controller.sendMessage(new SendChatMessageRequest(10L, "안녕하세요"), authentication);

            ArgumentCaptor<ChatMessageBroadcast> captor = ArgumentCaptor.forClass(ChatMessageBroadcast.class);
            verify(messagingTemplate).convertAndSend(any(String.class), captor.capture());
            ChatMessageBroadcast broadcast = captor.getValue();
            assertThat(broadcast.messageId()).isEqualTo(99L);
            assertThat(broadcast.chatRoomId()).isEqualTo(10L);
            assertThat(broadcast.senderId()).isEqualTo(1L);
            assertThat(broadcast.content()).isEqualTo("안녕하세요");
            assertThat(broadcast.read()).isFalse();
        }

        @Test
        @DisplayName("채팅방에 2명이 구독 중이면 읽음 처리 후 read=true로 브로드캐스트한다")
        void marksAsRead_andBroadcastsRead_whenBothSubscribed() {
            when(chatMessageUseCase.createMessage(any())).thenReturn(savedMessage());
            SimpSubscription sub1 = mock(SimpSubscription.class);
            SimpSubscription sub2 = mock(SimpSubscription.class);
            when(simpUserRegistry.findSubscriptions(any())).thenReturn(Set.of(sub1, sub2));

            controller.sendMessage(new SendChatMessageRequest(10L, "안녕하세요"), authentication);

            InOrder inOrder = inOrder(chatMessageUseCase, messagingTemplate);
            inOrder.verify(chatMessageUseCase).markAsRead(99L);
            ArgumentCaptor<ChatMessageBroadcast> captor = ArgumentCaptor.forClass(ChatMessageBroadcast.class);
            inOrder.verify(messagingTemplate).convertAndSend(any(String.class), captor.capture());
            assertThat(captor.getValue().read()).isTrue();
        }

        @Test
        @DisplayName("채팅방에 1명만 구독 중이면 읽음 처리를 하지 않고 read=false로 브로드캐스트한다")
        void doesNotMarkAsRead_whenOneSubscriber() {
            when(chatMessageUseCase.createMessage(any())).thenReturn(savedMessage());
            when(simpUserRegistry.findSubscriptions(any())).thenReturn(Set.of(mock(SimpSubscription.class)));

            controller.sendMessage(new SendChatMessageRequest(10L, "안녕하세요"), authentication);

            verify(chatMessageUseCase, never()).markAsRead(any());
            ArgumentCaptor<ChatMessageBroadcast> captor = ArgumentCaptor.forClass(ChatMessageBroadcast.class);
            verify(messagingTemplate).convertAndSend(any(String.class), captor.capture());
            assertThat(captor.getValue().read()).isFalse();
        }
    }

    @Nested
    @DisplayName("예외 처리 - @MessageExceptionHandler")
    class ExceptionHandler {

        @Test
        @DisplayName("ApplicationException 발생 시 에러 코드와 메시지를 담아 반환하고 메트릭을 기록한다")
        void handlesApplicationException() {
            ChatRoomNotFoundException e = new ChatRoomNotFoundException();

            ChatErrorResponse response = controller.handleApplicationException(e);

            assertThat(response.code()).isEqualTo(e.getErrorCode().getCode());
            assertThat(response.message()).isEqualTo(e.getMessage());
            assertThat(response.timestamp()).isNotNull();
            verify(chatMetricsPort).recordWebSocketError(e.getErrorCode().getCode());
        }

        @Test
        @DisplayName("예상치 못한 예외 발생 시 INTERNAL_ERROR를 반환하고 메트릭을 기록한다")
        void handlesUnexpectedException() {
            Exception e = new RuntimeException("DB 연결 실패");

            ChatErrorResponse response = controller.handleException(e);

            assertThat(response.code()).isEqualTo("INTERNAL_ERROR");
            assertThat(response.timestamp()).isNotNull();
            verify(chatMetricsPort).recordWebSocketError("INTERNAL_ERROR");
        }
    }
}
