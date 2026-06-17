package com.ssambbong.gymjjak.chat.application.query;

import com.ssambbong.gymjjak.chat.application.query.ChatRoomSummary;

import java.util.List;

public record ChatRoomListResult(
        int totalCount,
        long totalUnreadCount,
        List<ChatRoomSummary> chatRooms
) {}
