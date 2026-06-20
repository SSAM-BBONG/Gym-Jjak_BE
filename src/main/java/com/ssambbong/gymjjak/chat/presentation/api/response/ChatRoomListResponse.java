package com.ssambbong.gymjjak.chat.presentation.api.response;

import java.util.List;

public record ChatRoomListResponse(
        int totalCount,
        long totalUnreadCount,
        List<ChatRoomSummaryResponse> chatRooms
) {}
