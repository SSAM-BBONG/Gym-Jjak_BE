package com.ssambbong.gymjjak.chatbot.presentation.websocket.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendChatbotMessageRequest(
        String sessionId,
        @NotBlank @Size(max = 5000) String content,
        @Size(max = 50) String intentHint,
        ChatbotQuickReplySelection quickReply
) {
}
