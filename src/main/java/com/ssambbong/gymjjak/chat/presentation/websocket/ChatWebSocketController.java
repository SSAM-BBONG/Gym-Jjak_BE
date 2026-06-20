package com.ssambbong.gymjjak.chat.presentation.websocket;

import com.ssambbong.gymjjak.chat.application.command.SendChatMessageCommand;
import com.ssambbong.gymjjak.chat.application.usecase.ChatMessageUseCase;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;
import com.ssambbong.gymjjak.chat.presentation.websocket.request.SendChatMessageRequest;
import com.ssambbong.gymjjak.chat.presentation.websocket.response.ChatMessageBroadcast;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageUseCase chatMessageUseCase;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload @Valid SendChatMessageRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
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

        messagingTemplate.convertAndSend(destination, new ChatMessageBroadcast(
                saved.getId(),
                saved.getChatRoomId(),
                saved.getSenderId(),
                saved.getContent(),
                isRead,
                saved.getCreatedAt()
        ));
    }
}
