package com.ssambbong.gymjjak.chatbot.presentation.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FindChatbotMessagesRequest(
        String cursor,
        @Min(1) @Max(50) Integer size
) {
    private static final int DEFAULT_SIZE = 20;

    public int resolveSize() {
        return size == null ? DEFAULT_SIZE : size;
    }
}
