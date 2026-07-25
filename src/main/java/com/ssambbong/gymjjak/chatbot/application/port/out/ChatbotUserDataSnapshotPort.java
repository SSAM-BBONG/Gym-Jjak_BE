package com.ssambbong.gymjjak.chatbot.application.port.out;

public interface ChatbotUserDataSnapshotPort {
    ChatbotUserDataSnapshot load(Long userId);
}
