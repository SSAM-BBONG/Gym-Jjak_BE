package com.ssambbong.gymjjak.chatbot.presentation.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FindChatbotSessionsRequest(
        String cursor,
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 50, message = "size는 최대 50까지 가능합니다.")
        Integer size
) {
    public int resolveSize() {
        return size == null ? 20 : size;
    }
}
