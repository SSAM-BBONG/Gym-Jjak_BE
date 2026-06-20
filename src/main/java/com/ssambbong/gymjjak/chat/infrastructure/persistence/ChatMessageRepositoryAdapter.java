package com.ssambbong.gymjjak.chat.infrastructure.persistence;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageItem;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;
import com.ssambbong.gymjjak.chat.domain.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryAdapter implements ChatMessageRepository {

    private final SpringDataChatMessageRepository repository;

    @Override
    public ChatMessage save(ChatMessage message) {
        return repository.save(ChatMessageJpaEntity.from(message)).toDomain();
    }

    @Override
    public void markAsRead(Long messageId) {
        repository.markAsRead(messageId);
    }

    @Override
    public ChatMessageListResult findMessages(ChatMessageQuery query, Long readerId) {
        repository.markMessagesAsRead(query.chatRoomId(), readerId);

        List<ChatMessageProjection> rows = query.cursor() == null
                ? repository.findLatestMessages(query.chatRoomId(), query.size() + 1)
                : repository.findMessagesBeforeCursor(query.chatRoomId(), query.cursor(), query.size() + 1);

        boolean hasNext = rows.size() > query.size();
        List<ChatMessageItem> messages = rows.stream()
                .limit(query.size())
                .map(p -> new ChatMessageItem(
                        p.getChatMessageId(),
                        p.getSenderId(),
                        p.getContent(),
                        p.getIsRead(),
                        p.getCreatedAt()
                ))
                .toList();

        Long nextCursor = hasNext ? messages.get(messages.size() - 1).messageId() : null;

        return new ChatMessageListResult(messages, nextCursor, hasNext);
    }
}
