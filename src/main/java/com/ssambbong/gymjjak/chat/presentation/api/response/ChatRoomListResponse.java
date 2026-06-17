package com.ssambbong.gymjjak.chat.presentation.api.response;

import com.ssambbong.gymjjak.chat.application.query.ChatRoomListResult;

import java.util.List;

public record ChatRoomListResponse(
        int totalCount,
        long totalUnreadCount,
        List<ChatRoomSummaryResponse> chatRooms
) {
    public static ChatRoomListResponse from(ChatRoomListResult result) {
        return new ChatRoomListResponse(
                result.totalCount(),
                result.totalUnreadCount(),
                result.chatRooms().stream()
                        .map(ChatRoomSummaryResponse::from)
                        .toList()
        );
    }
}
