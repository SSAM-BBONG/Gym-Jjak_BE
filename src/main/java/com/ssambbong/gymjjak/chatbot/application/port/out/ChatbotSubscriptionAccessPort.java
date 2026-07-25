package com.ssambbong.gymjjak.chatbot.application.port.out;

public interface ChatbotSubscriptionAccessPort {

    // 챗봇 접근 권한 검증(활성·미만료 구독권 또는 ACTIVE 트레이너 프로필)
    boolean hasActiveAccess(Long userId);
}
