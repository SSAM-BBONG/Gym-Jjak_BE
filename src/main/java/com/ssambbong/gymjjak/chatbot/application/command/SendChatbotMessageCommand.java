package com.ssambbong.gymjjak.chatbot.application.command;

public record SendChatbotMessageCommand(
        String sessionId,
        Long userId,
        String userRole,
        String content,
        String intentHint
) {
}
