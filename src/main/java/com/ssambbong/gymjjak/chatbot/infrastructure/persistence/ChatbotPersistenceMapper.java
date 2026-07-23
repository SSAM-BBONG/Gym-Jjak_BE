package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
import org.springframework.stereotype.Component;

@Component
public class ChatbotPersistenceMapper {

    public ChatbotSession toDomain(ChatbotSessionJpaEntity entity) {
        return new ChatbotSession(entity.getSessionId(), entity.getUserId(), entity.getLastActivityAt());
    }

    public ChatbotSessionSummary toSummary(ChatbotSessionListRow row) {
        return new ChatbotSessionSummary(
                row.getSessionId(), row.getTitle(), row.getLastMessage(), row.getLastActivityAt()
        );
    }

    public ChatbotMessage toDomain(ChatbotMessageJpaEntity entity) {
        return new ChatbotMessage(
                entity.getId(), entity.getSessionId(), entity.getRole(), entity.getContent(), entity.getIntentHint(),
                entity.getCategory(), entity.getRoutineJson(), entity.getSourcesJson(), entity.getLimited(), entity.getCreatedAt()
        );
    }
}
