package com.ssambbong.gymjjak.chatbot.presentation.websocket.request;

import jakarta.validation.constraints.NotBlank;

public record ChatbotQuickReplySelection(
        @NotBlank String questionId,
        @NotBlank String value
) {
}
