package com.ssambbong.gymjjak.payments.subscription.application.service;

import com.ssambbong.gymjjak.payments.subscription.application.usecase.SubscriptionQueryUseCase;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionQueryService implements SubscriptionQueryUseCase {

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
}
