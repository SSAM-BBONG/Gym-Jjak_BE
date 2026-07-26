package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotSubscriptionAccessPort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatbotSubscriptionAccessAdapter implements ChatbotSubscriptionAccessPort {

    private final SpringDataSubscriptionRepository subscriptionRepository;
    private final SpringDataTrainerProfileRepository trainerProfileRepository;
    private final Clock clock;

    @Override
    public boolean hasActiveAccess(Long userId) {
        boolean hasActiveSubscription = subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                userId, SubscriptionStatus.ACTIVE, LocalDateTime.now(clock)
        );
        return hasActiveSubscription
                || trainerProfileRepository.existsByUserIdAndStatus(userId, TrainerProfileStatus.ACTIVE);
    }
}
