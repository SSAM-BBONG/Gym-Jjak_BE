package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatbotMessagePersistenceAdapter implements ChatbotMessageRepository {

    private final SpringDataChatbotMessageRepository springDataChatbotMessageRepository;
    private final ChatbotPersistenceMapper mapper;

    @Override
    public List<ChatbotMessage> findHistory(
            String sessionId,
            LocalDateTime cursorCreatedAt,
            Long cursorMessageId,
            int limit
    ) {
        return springDataChatbotMessageRepository.findHistory(
                        sessionId,
                        cursorCreatedAt,
                        cursorMessageId,
                        PageRequest.of(0, limit)
                ).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
