package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatbotSessionPersistenceAdapter implements ChatbotSessionRepository {

    private final SpringDataChatbotSessionRepository repository;
    private final ChatbotPersistenceMapper persistenceMapper;

    @Override
    public Optional<ChatbotSession> findBySessionId(String sessionId) {
        return repository.findBySessionId(sessionId).map(persistenceMapper::toDomain);
    }

    @Override
    public List<ChatbotSessionSummary> findSessionSummaries(
            Long userId,
            LocalDateTime cursorLastActivityAt,
            String cursorSessionId,
            int limit
    ) {
        return repository.findSessionList(userId, cursorLastActivityAt, cursorSessionId, limit).stream()
                .map(persistenceMapper::toSummary)
                .toList();
    }
}
