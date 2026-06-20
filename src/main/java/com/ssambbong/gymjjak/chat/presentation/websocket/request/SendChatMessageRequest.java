package com.ssambbong.gymjjak.chat.presentation.websocket.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SendChatMessageRequest(
        @NotNull @Positive Long chatRoomId,
        @NotBlank @Size(max = 2000) String content
) {}
