package com.ssambbong.gymjjak.chat.presentation.websocket;

import com.ssambbong.gymjjak.chat.application.command.SendChatMessageCommand;
import com.ssambbong.gymjjak.chat.application.port.ChatMetricsPort;
import com.ssambbong.gymjjak.chat.application.usecase.ChatMessageUseCase;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;
import com.ssambbong.gymjjak.chat.presentation.websocket.request.SendChatMessageRequest;
import com.ssambbong.gymjjak.chat.presentation.websocket.response.ChatErrorResponse;
import com.ssambbong.gymjjak.chat.presentation.websocket.response.ChatMessageBroadcast;
import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageUseCase chatMessageUseCase;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;
    private final ChatMetricsPort chatMetricsPort;

    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload @Valid SendChatMessageRequest request,
            Principal principal
    ) {
        AuthUser authUser = (AuthUser) ((Authentication) principal).getPrincipal();
        SendChatMessageCommand command = new SendChatMessageCommand(
                request.chatRoomId(),
                authUser.userId(),
                request.content()
        );

        ChatMessage saved = chatMessageUseCase.createMessage(command);

        String destination = "/topic/chat.room." + saved.getChatRoomId();
        boolean isRead = simpUserRegistry.findSubscriptions(
                s -> s.getDestination().equals(destination)
        ).size() == 2;

        if (isRead) {
            chatMessageUseCase.markAsRead(saved.getId());
        }

        messagingTemplate.convertAndSend(destination, ChatMessageBroadcast.from(saved, isRead));
    }

    @MessageExceptionHandler(ApplicationException.class)
    @SendToUser("/queue/errors")
    public ChatErrorResponse handleApplicationException(ApplicationException e) {
        log.warn("event=websocket_exception_handled code={} message={}", e.getErrorCode().getCode(), e.getMessage());
        chatMetricsPort.recordWebSocketError(e.getErrorCode().getCode());
        return new ChatErrorResponse(LocalDateTime.now(), e.getErrorCode().getCode(), e.getMessage());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ChatErrorResponse handleException(Exception e) {
        log.error("event=websocket_exception_handled reason=unexpected_exception exceptionClass={}", e.getClass().getSimpleName(), e);
        chatMetricsPort.recordWebSocketError("INTERNAL_ERROR");
        return new ChatErrorResponse(LocalDateTime.now(), "INTERNAL_ERROR", "메시지 처리 중 오류가 발생했습니다.");
    }
}
