package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotSubscriptionAccessPort;
import com.ssambbong.gymjjak.payments.subscription.application.usecase.SubscriptionQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatbotSubscriptionAccessAdapter implements ChatbotSubscriptionAccessPort {

    private final SubscriptionQueryUseCase subscriptionQueryUseCase;

    @Override
    public boolean hasActiveAccess(Long userId) {
        // 구독 여부 검증
        return subscriptionQueryUseCase.findMySubscription(userId).isPresent();
    }
}
