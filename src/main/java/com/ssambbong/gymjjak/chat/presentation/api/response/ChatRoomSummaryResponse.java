package com.ssambbong.gymjjak.chat.presentation.api.response;

import com.ssambbong.gymjjak.chat.application.query.ChatRoomSummary;

import java.time.LocalDateTime;

public record ChatRoomSummaryResponse(
        Long chatRoomId,
        String partnerName,
        String partnerRole,
        String partnerProfileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageAt,
        long unreadCount
) {
    public static ChatRoomSummaryResponse from(ChatRoomSummary summary) {
        return new ChatRoomSummaryResponse(
                summary.chatRoomId(),
                summary.partnerName(),
                summary.partnerRole(),
                summary.partnerProfileImageUrl(),
                summary.lastMessage(),
                summary.lastMessageAt(),
                summary.unreadCount()
        );
    }
}
