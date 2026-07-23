package com.ssambbong.gymjjak.chatbot.domain.repository;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatbotMessageRepository {

    List<ChatbotMessage> findHistory(
            String sessionId,
            LocalDateTime cursorCreatedAt,
            Long cursorMessageId,
            int limit
    );
}
