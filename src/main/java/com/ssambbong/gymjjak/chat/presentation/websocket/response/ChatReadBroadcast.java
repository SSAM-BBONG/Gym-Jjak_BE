package com.ssambbong.gymjjak.chat.presentation.websocket.response;

public record ChatReadBroadcast(
        String type,
        Long chatRoomId
) {
    public static ChatReadBroadcast of(Long chatRoomId) {
        return new ChatReadBroadcast("READ", chatRoomId);
    }
}
