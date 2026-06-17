package com.ssambbong.gymjjak.chat.presentation.api.response;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageItem;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long senderId,
        String content,
        Boolean read,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessageItem item) {
        return new ChatMessageResponse(
                item.messageId(),
                item.senderId(),
                item.content(),
                item.read(),
                item.createdAt()
        );
    }
}
