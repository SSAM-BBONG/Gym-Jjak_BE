package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
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
}
