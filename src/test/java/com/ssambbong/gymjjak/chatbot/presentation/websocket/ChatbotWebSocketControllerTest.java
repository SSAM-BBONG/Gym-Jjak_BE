package com.ssambbong.gymjjak.chatbot.presentation.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiClientPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
import com.ssambbong.gymjjak.chatbot.application.service.ChatbotConversationService;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.request.SendChatbotMessageRequest;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotDeltaEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotDoneEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotStartedEvent;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotWebSocketControllerTest {

    @Mock private ChatbotConversationService conversationService;
    @Mock private ChatbotAiClientPort chatbotAiClientPort;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @Test
    void relaysStartedDeltaAndDoneAndPersistsOnlyDone() {
        TaskExecutor directExecutor = Runnable::run;
        ChatbotWebSocketController controller = new ChatbotWebSocketController(
                conversationService, chatbotAiClientPort, messagingTemplate, new ObjectMapper(), directExecutor
        );
        ChatbotConversationStart start = start();
        AuthUser authUser = new AuthUser(7L, "member@example.com", "USER");
        when(conversationService.prepare(any())).thenReturn(start);
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Consumer<ChatbotAiEvent> consumer = invocation.getArgument(1);
            consumer.accept(new ChatbotAiEvent.Delta("이번 주는 "));
            consumer.accept(new ChatbotAiEvent.Done(
                    "session-123", "하체 운동을 2회로 나눠보세요.", "ROUTINE", null, "[]", false, "[]"
            ));
            return null;
        }).when(chatbotAiClientPort).stream(eq(start.fastApiRequest()), any());

        controller.sendMessage(
                new SendChatbotMessageRequest("session-123", "루틴 추천", "ROUTINE_RECOMMENDATION", null),
                new UsernamePasswordAuthenticationToken(authUser, null)
        );

        ArgumentCaptor<Object> events = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate, times(3)).convertAndSendToUser(eq("7"), eq("/queue/chatbot"), events.capture());
        verify(conversationService).persistDone(eq(start), any(ChatbotAiEvent.Done.class));
        verify(conversationService).releaseStreamLock(start);
        assertThat(events.getAllValues()).contains(
                ChatbotStartedEvent.of("session-123", "request-123"),
                ChatbotDeltaEvent.of("session-123", "request-123", "이번 주는 "),
                ChatbotDoneEvent.of("request-123", new ChatbotAiEvent.Done(
                        "session-123", "하체 운동을 2회로 나눠보세요.", "ROUTINE", null, "[]", false, "[]"
                ), new ObjectMapper())
        );
    }

    @Test
    void exposesDoneQuickRepliesAsJsonArray() {
        ChatbotDoneEvent event = ChatbotDoneEvent.of(
                "request-123",
                new ChatbotAiEvent.Done(
                        "session-123", "목표를 선택해 주세요.", "ROUTINE", null, "[]", true,
                        "[{\"question_id\":\"ROUTINE_GOAL\",\"label\":\"근육 증가\",\"value\":\"MUSCLE_GAIN\"}]"
                ),
                new ObjectMapper()
        );

        assertThat(event.quickReplies().isArray()).isTrue();
        assertThat(event.quickReplies().get(0).get("questionId").asText()).isEqualTo("ROUTINE_GOAL");
        assertThat(event.quickReplies().get(0).get("value").asText()).isEqualTo("MUSCLE_GAIN");
    }

    private ChatbotConversationStart start() {
        return new ChatbotConversationStart(
                "session-123",
                "request-123",
                new ChatbotAiRequest(
                        "session-123", "루틴 추천", "ROUTINE_RECOMMENDATION",
                        new ChatbotAiRequest.Actor(7L, "USER"),
                        new ChatbotAiRequest.Memory(null, List.of(), List.of()),
                        "request-123"
                )
        );
    }
}
