package com.ssambbong.gymjjak.chat.presentation.websocket.response;

import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageBroadcast(
        String type,
        Long messageId,
        Long chatRoomId,
        Long senderId,
        String content,
        boolean read,
        LocalDateTime createdAt
) {
    public static ChatMessageBroadcast from(ChatMessage message) {
        return new ChatMessageBroadcast(
                "MESSAGE",
                message.getId(),
                message.getChatRoomId(),
                message.getSenderId(),
                message.getContent(),
                false,
                message.getCreatedAt()
        );
    }
}
