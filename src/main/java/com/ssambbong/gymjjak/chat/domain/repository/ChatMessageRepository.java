package com.ssambbong.gymjjak.chat.domain.repository;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;

import java.util.Optional;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage message);
    Optional<ChatMessage> findById(Long id);
    void markAsRead(Long messageId);
    ChatMessageListResult findMessages(ChatMessageQuery query, Long readerId);
}
