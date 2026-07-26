package com.ssambbong.gymjjak.chatbot.application.command;

import com.ssambbong.gymjjak.chatbot.presentation.websocket.request.ChatbotQuickReplySelection;

public record SendChatbotMessageCommand(
        String sessionId,
        Long userId,
        String userRole,
        String content,
        String intentHint,
        ChatbotQuickReplySelection quickReply
) {
}
