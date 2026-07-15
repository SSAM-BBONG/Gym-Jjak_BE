package com.ssambbong.gymjjak.payments.subscription.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssambbong.gymjjak.payments.subscription.application.usecase.SubscriptionQueryUseCase;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "내 구독 조회 응답")
public record SubscriptionMeResponse(

        @Schema(description = "플랜 유형", example = "MONTHLY", allowableValues = {"MONTHLY", "YEARLY"})
        SubscriptionPlanType planType,

        @Schema(description = "구독 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "EXPIRED"})
        SubscriptionStatus status,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "구독 시작일", example = "2026-07-01T00:00:00")
        LocalDateTime startedAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "구독 만료일", example = "2026-08-01T00:00:00")
        LocalDateTime expiredAt

) {
    public static SubscriptionMeResponse from(SubscriptionQueryUseCase.SubscriptionView view) {
        return new SubscriptionMeResponse(
                view.planType(),
                view.status(),
                view.startedAt(),
                view.expiredAt()
        );
    }
}
