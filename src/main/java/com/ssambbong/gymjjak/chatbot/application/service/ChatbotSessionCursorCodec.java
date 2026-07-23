package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotSessionCursorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ChatbotSessionCursorCodec {

    private final ObjectMapper objectMapper;

    public String encode(LocalDateTime lastActivityAt, String sessionId) {
        try {
            String payload = objectMapper.writeValueAsString(new CursorPayload(lastActivityAt, sessionId));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException exception) {
            throw new InvalidChatbotSessionCursorException();
        }
    }

    public CursorPayload decode(String cursor) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            CursorPayload payload = objectMapper.readValue(decoded, CursorPayload.class);
            if (payload.lastActivityAt() == null || payload.sessionId() == null || payload.sessionId().isBlank()) {
                throw new InvalidChatbotSessionCursorException();
            }
            return payload;
        } catch (IllegalArgumentException | IOException exception) {
            throw new InvalidChatbotSessionCursorException();
        }
    }

    public record CursorPayload(LocalDateTime lastActivityAt, String sessionId) {
    }
}
