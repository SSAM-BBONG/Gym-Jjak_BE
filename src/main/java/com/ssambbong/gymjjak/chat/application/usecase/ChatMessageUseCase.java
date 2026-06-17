package com.ssambbong.gymjjak.chat.application.usecase;

import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;

public interface ChatMessageUseCase {
    ChatMessageListResult getMessages(Long requesterId, ChatMessageQuery query);
}
