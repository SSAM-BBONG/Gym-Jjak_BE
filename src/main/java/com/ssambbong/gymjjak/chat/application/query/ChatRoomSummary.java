package com.ssambbong.gymjjak.chat.application.query;

import java.time.LocalDateTime;

public record ChatRoomSummary(
        Long chatRoomId,
        String partnerName,
        String partnerRole,
        String partnerProfileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageAt,
        long unreadCount
) {}
