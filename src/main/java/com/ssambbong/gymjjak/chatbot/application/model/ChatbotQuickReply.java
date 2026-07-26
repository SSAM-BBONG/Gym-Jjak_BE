package com.ssambbong.gymjjak.chatbot.application.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record ChatbotQuickReply(
        @JsonAlias("question_id") String questionId,
        String label,
        String value
) {
}
