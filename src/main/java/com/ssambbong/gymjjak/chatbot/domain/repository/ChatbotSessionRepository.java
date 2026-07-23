package com.ssambbong.gymjjak.chatbot.domain.repository;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatbotSessionRepository {

    Optional<ChatbotSession> findBySessionId(String sessionId);

    List<ChatbotSessionSummary> findSessionSummaries(
            Long userId,
            LocalDateTime cursorLastActivityAt,
            String cursorSessionId,
            int limit
    );
}
