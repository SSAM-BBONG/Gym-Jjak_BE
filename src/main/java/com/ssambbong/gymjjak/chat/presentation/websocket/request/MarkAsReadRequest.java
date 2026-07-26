package com.ssambbong.gymjjak.chat.presentation.websocket.request;

import jakarta.validation.constraints.NotNull;

public record MarkAsReadRequest(
        @NotNull Long chatRoomId
) {
}
