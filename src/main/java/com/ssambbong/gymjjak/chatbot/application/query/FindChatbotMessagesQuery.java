package com.ssambbong.gymjjak.chatbot.application.query;

public record FindChatbotMessagesQuery(Long userId, String sessionId, String cursor, int size) {
}
