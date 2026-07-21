package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.port.out.AiNutritionAccessPort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AiNutritionAccessAdapter implements AiNutritionAccessPort {
    private final SpringDataUserRepository userRepository;
    private final SpringDataSubscriptionRepository subscriptionRepository;
    private final Clock clock;

    @Override
    public boolean hasActiveAccess(Long userId) {
        // 트레이너는 회원의 식단 관리를 위해 결제 여부와 관계없이 AI 식단 분석을 사용할 수 있다.
        if (userRepository.existsByIdAndRole(userId, UserRole.TRAINER)) {
            return true;
        }

        // ACTIVE 상태이면서 만료 시각이 현재보다 뒤인 구독만 유효하게 본다.
        return subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                userId, SubscriptionStatus.ACTIVE, LocalDateTime.now(clock));
    }
}
