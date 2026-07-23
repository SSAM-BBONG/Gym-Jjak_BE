package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotMessageCursorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ChatbotMessageCursorCodec {

    private final ObjectMapper objectMapper;

    public String encode(LocalDateTime createdAt, Long messageId) {
        try {
            String json = objectMapper.writeValueAsString(new CursorPayload(createdAt, messageId));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("챗봇 메시지 커서를 생성할 수 없습니다.", exception);
        }
    }

    public CursorPayload decode(String cursor) {
        try {
            String json = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            CursorPayload payload = objectMapper.readValue(json, CursorPayload.class);
            if (payload == null || payload.createdAt() == null || payload.messageId() == null) {
                throw new InvalidChatbotMessageCursorException();
            }
            return payload;
        } catch (IllegalArgumentException | JsonProcessingException exception) {
            throw new InvalidChatbotMessageCursorException();
        }
    }

    public record CursorPayload(LocalDateTime createdAt, Long messageId) {
    }
}
