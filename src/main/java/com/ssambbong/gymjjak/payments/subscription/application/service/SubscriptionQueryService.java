package com.ssambbong.gymjjak.payments.subscription.application.service;

import com.ssambbong.gymjjak.payments.subscription.domain.repository.SubscriptionRepository;
import com.ssambbong.gymjjak.payments.subscription.application.usecase.SubscriptionQueryUseCase;
import com.ssambbong.gymjjak.payments.subscription.domain.model.Subscription;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionQueryService implements SubscriptionQueryUseCase {

    private final SubscriptionRepository subscriptionRepository;

    private static final Map<SubscriptionPlanType, Integer> PRICES = Map.of(
            SubscriptionPlanType.MONTHLY, 7900,
            SubscriptionPlanType.YEARLY, 79000
    );

    // 구독 플랜 조회
    @Override
    public List<PlanView> findPlans() {
        log.debug("event=subscription_plans_fetch");
        List<PlanView> plans = Arrays.stream(SubscriptionPlanType.values())
                .map(plan -> new PlanView(plan, PRICES.get(plan)))
                .toList();
        log.info("event=subscription_plans_fetch_succeeded count={}", plans.size());
        return plans;
    }

    // 내 활성 구독 조회 (없으면 empty)
    @Override
    public Optional<SubscriptionView> findMySubscription(Long userId) {
        log.debug("event=subscription_fetch userId={}", userId);
        Optional<SubscriptionView> result = subscriptionRepository.findActiveByUserId(userId)
                .map(this::toView);
        log.info("event=subscription_fetch_succeeded userId={} found={}", userId, result.isPresent());
        return result;
    }

    private SubscriptionView toView(Subscription subscription) {
        return new SubscriptionView(
                subscription.getPlanType(),
                subscription.getStatus(),
                subscription.getStartedAt(),
                subscription.getExpiredAt()
        );
    }
}
