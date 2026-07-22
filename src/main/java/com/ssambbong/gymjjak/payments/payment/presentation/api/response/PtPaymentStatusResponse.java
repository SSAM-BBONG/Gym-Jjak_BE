package com.ssambbong.gymjjak.payments.payment.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "PT 구매 상태 응답")
public record PtPaymentStatusResponse(

        @Schema(description = "해당 PT 코스의 유효한 구매 여부 (PAID 상태인 결제 존재 시 true)", example = "true")
        boolean isPurchased

) {
    public static PtPaymentStatusResponse of(boolean isPurchased) {
        return new PtPaymentStatusResponse(isPurchased);
    }
}
