package com.ssambbong.gymjjak.payments.payment.presentation.api.request;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "구독 결제 요청")
public record CreateSubscriptionPaymentRequest(
        @Schema(description = "구독 플랜 타입", example = "MONTHLY")
        @NotNull SubscriptionPlanType planType
) {}
