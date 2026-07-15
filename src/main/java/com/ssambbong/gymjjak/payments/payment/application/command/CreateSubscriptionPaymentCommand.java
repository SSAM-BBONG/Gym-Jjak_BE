package com.ssambbong.gymjjak.payments.payment.application.command;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;

// 구독 결제 요청 생성
public record CreateSubscriptionPaymentCommand(
        Long userId,
        SubscriptionPlanType planType
) {}
