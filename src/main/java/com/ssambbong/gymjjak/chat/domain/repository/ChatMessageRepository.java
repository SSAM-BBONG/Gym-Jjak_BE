package com.ssambbong.gymjjak.chat.domain.repository;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;

public interface ChatMessageRepository {
    ChatMessageListResult findMessages(ChatMessageQuery query);
}
