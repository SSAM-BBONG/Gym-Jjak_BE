package com.ssambbong.gymjjak.chatbot.application.usecase;

import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;

public interface ChatbotSessionQueryUseCase {

    ChatbotSessionListResult findSessions(FindChatbotSessionsQuery query);
}
