package com.ssambbong.gymjjak.chatbot.application.query;

public record FindChatbotSessionsQuery(Long userId, String cursor, int size) {
}
