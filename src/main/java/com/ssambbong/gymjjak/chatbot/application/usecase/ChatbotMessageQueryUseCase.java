package com.ssambbong.gymjjak.chatbot.application.usecase;

import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;

public interface ChatbotMessageQueryUseCase {

    ChatbotMessageHistoryResult findMessages(FindChatbotMessagesQuery query);
}
