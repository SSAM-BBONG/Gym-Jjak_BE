package com.ssambbong.gymjjak.chatbot.presentation.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiClientPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
import com.ssambbong.gymjjak.chatbot.application.service.ChatbotConversationService;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.request.SendChatbotMessageRequest;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotDeltaEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotDoneEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotErrorEvent;
import com.ssambbong.gymjjak.chatbot.presentation.websocket.response.ChatbotStartedEvent;
import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatbotWebSocketController {

    private static final String USER_DESTINATION = "/queue/chatbot";

    private final ChatbotConversationService conversationService;
    private final ChatbotAiClientPort chatbotAiClientPort;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    @Qualifier("chatbotStreamingTaskExecutor")
    private final TaskExecutor chatbotStreamingTaskExecutor;

    @MessageMapping("/chatbot.send")
    public void sendMessage(@Payload @Valid SendChatbotMessageRequest request, Principal principal) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();
        ChatbotConversationStart start = conversationService.prepare(new SendChatbotMessageCommand(
                request.sessionId(), authUser.userId(), authUser.role(), request.content(), request.intentHint(), request.quickReply()
        ));

        send(authUser, ChatbotStartedEvent.of(start.sessionId(), start.requestId()));
        try {
            chatbotStreamingTaskExecutor.execute(() -> stream(authUser, start));
        } catch (RuntimeException exception) {
            conversationService.releaseStreamLock(start);
            log.warn("event=chatbot_stream_rejected requestId={} exception={}", start.requestId(), exception.toString());
            send(authUser, ChatbotErrorEvent.of(
                    start.sessionId(), start.requestId(), "CHATBOT_STREAM_CAPACITY_EXCEEDED",
                    "챗봇 응답 처리 용량이 초과되었습니다.", true
            ));
        }
    }

    private void stream(AuthUser authUser, ChatbotConversationStart start) {
        try {
            chatbotAiClientPort.stream(start.fastApiRequest(), event -> handleFastApiEvent(authUser, start, event));
        } catch (ApplicationException exception) {
            send(authUser, ChatbotErrorEvent.of(
                    start.sessionId(), start.requestId(), exception.getErrorCode().getCode(), exception.getMessage(), true
            ));
        } catch (RuntimeException exception) {
            log.error("event=chatbot_stream_failed requestId={}", start.requestId(), exception);
            send(authUser, ChatbotErrorEvent.of(
                    start.sessionId(), start.requestId(), "INTERNAL_ERROR", "챗봇 응답 생성 중 오류가 발생했습니다.", true
            ));
        } finally {
            conversationService.releaseStreamLock(start);
        }
    }

    private void handleFastApiEvent(AuthUser authUser, ChatbotConversationStart start, ChatbotAiEvent event) {
        if (event instanceof ChatbotAiEvent.Delta delta) {
            send(authUser, ChatbotDeltaEvent.of(start.sessionId(), start.requestId(), delta.text()));
            return;
        }
        if (event instanceof ChatbotAiEvent.Done done) {
            conversationService.persistDone(start, done);
            send(authUser, ChatbotDoneEvent.of(start.requestId(), done, objectMapper));
            return;
        }
        ChatbotAiEvent.Error error = (ChatbotAiEvent.Error) event;
        send(authUser, ChatbotErrorEvent.of(
                start.sessionId(), start.requestId(), error.code(), error.message(), error.retryable()
        ));
    }

    private void send(AuthUser authUser, Object event) {
        messagingTemplate.convertAndSendToUser(authUser.getName(), USER_DESTINATION, event);
    }

    @MessageExceptionHandler(ApplicationException.class)
    @SendToUser(USER_DESTINATION)
    public ChatbotErrorEvent handleApplicationException(ApplicationException exception) {
        return ChatbotErrorEvent.of(null, null, exception.getErrorCode().getCode(), exception.getMessage(), false);
    }
}
