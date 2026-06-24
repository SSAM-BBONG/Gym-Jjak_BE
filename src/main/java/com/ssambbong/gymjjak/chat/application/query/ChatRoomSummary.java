package com.ssambbong.gymjjak.chat.application.query;

import java.time.LocalDateTime;

public record ChatRoomSummary(
        Long chatRoomId,
        String partnerName,
        String partnerRole,
        Long partnerProfileFileId,
        String partnerProfileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageAt,
        long unreadCount
) {
    public ChatRoomSummary withProfileImageUrl(String url) {
        return new ChatRoomSummary(
                chatRoomId, partnerName, partnerRole,
                partnerProfileFileId, url,
                lastMessage, lastMessageAt, unreadCount
        );
    }
}
