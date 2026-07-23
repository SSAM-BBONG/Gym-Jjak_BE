package com.ssambbong.gymjjak.chatbot.application.port.out;

public interface ChatbotSubscriptionAccessPort {

    // 챗봇 - 활성 구독 여부 검증
    boolean hasActiveAccess(Long userId);
}
