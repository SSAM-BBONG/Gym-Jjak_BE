package com.ssambbong.gymjjak.chatbot.presentation.websocket.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.model.ChatbotQuickReply;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;

import java.util.List;

public record ChatbotDoneEvent(
        String type,
        String sessionId,
        String requestId,
        String answer,
        String category,
        String routine,
        String sources,
        boolean limited,
        JsonNode quickReplies
) {
    public static ChatbotDoneEvent of(String requestId, ChatbotAiEvent.Done done, ObjectMapper objectMapper) {
        return new ChatbotDoneEvent(
                "done", done.sessionId(), requestId, done.answer(), done.category(),
                done.routineJson(), done.sourcesJson(), done.limited(), parseQuickReplies(done.quickRepliesJson(), objectMapper)
        );
    }

    private static JsonNode parseQuickReplies(String quickRepliesJson, ObjectMapper objectMapper) {
        try {
            List<ChatbotQuickReply> quickReplies = objectMapper.readValue(
                    quickRepliesJson,
                    new TypeReference<>() {
                    }
            );
            return objectMapper.valueToTree(quickReplies);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid quick replies payload", exception);
        }
    }
}
