package com.ssambbong.gymjjak.chat.domain.repository;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage message);
    void markAsRead(Long messageId);
    ChatMessageListResult findMessages(ChatMessageQuery query, Long readerId);
}
