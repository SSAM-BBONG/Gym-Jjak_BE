package com.ssambbong.gymjjak.chat.application.usecase;

import com.ssambbong.gymjjak.chat.application.command.SendChatMessageCommand;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageListResult;
import com.ssambbong.gymjjak.chat.application.query.ChatMessageQuery;
import com.ssambbong.gymjjak.chat.domain.model.ChatMessage;

public interface ChatMessageUseCase {
    ChatMessage sendMessage(SendChatMessageCommand command);
    ChatMessageListResult getMessages(Long requesterId, ChatMessageQuery query);
}
