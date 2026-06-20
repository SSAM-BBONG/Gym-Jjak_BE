package com.ssambbong.gymjjak.chat.application.port;

public interface ChatMetricsPort {
    void recordMessageSent();
    void recordChatRoomCreated();
    void recordWebSocketError(String errorType);
}
