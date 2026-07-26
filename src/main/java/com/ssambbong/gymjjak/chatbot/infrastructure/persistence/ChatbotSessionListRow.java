package com.ssambbong.gymjjak.chatbot.infrastructure.persistence;

import java.time.LocalDateTime;

public interface ChatbotSessionListRow {

    String getSessionId();

    String getTitle();

    String getLastMessage();

    LocalDateTime getLastActivityAt();
}
