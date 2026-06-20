package com.ssambbong.gymjjak.chat.presentation.api.response;

import java.time.LocalDateTime;

public record ChatRoomSummaryResponse(
        Long chatRoomId,
        String partnerName,
        String partnerRole,
        String partnerProfileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageAt,
        long unreadCount
) {}
