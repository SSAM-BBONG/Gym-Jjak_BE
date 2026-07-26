package com.ssambbong.gymjjak.chatbot.application.result;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;

public record ChatbotConversationStart(
        String sessionId,
        String requestId,
        ChatbotAiRequest fastApiRequest
) {
}
