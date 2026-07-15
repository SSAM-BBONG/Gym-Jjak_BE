package com.ssambbong.gymjjak.payments.subscription.presentation.api.response;

import com.ssambbong.gymjjak.payments.subscription.application.usecase.SubscriptionQueryUseCase;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "구독 플랜 목록 응답")
public record SubscriptionPlansResponse(

        @Schema(description = "구독 플랜 목록")
        List<PlanItem> plans

) {
    public static SubscriptionPlansResponse from(List<SubscriptionQueryUseCase.PlanView> views) {
        return new SubscriptionPlansResponse(
                views.stream()
                        .map(v -> new PlanItem(v.planType(), v.price()))
                        .toList()
        );
    }

    @Schema(description = "구독 플랜 항목")
    public record PlanItem(

            @Schema(description = "플랜 유형", example = "MONTHLY", allowableValues = {"MONTHLY", "YEARLY"})
            SubscriptionPlanType planType,

            @Schema(description = "가격 (원)", example = "7900")
            int price

    ) {}
}
