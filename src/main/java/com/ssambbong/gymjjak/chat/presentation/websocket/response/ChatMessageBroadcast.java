package com.ssambbong.gymjjak.chat.presentation.websocket.response;

import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageBroadcast(
        Long messageId,
        Long chatRoomId,
        Long senderId,
        String content,
        boolean read,
        LocalDateTime createdAt
) {
    public static ChatMessageBroadcast from(ChatMessage message, boolean isRead) {
        return new ChatMessageBroadcast(
                message.getId(),
                message.getChatRoomId(),
                message.getSenderId(),
                message.getContent(),
                isRead,
                message.getCreatedAt()
        );
    }
}
