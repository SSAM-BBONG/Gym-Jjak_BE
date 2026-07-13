package com.ssambbong.gymjjak.payments.payment.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "PT 결제 요청")
public record CreatePtPaymentRequest(
        @Schema(description = "결제할 PT 코스 ID", example = "1")
        @NotNull Long ptCourseId
) {}
