package com.ssambbong.gymjjak.payments.payment.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "PortOne 웹훅 요청")
public record WebhookPaymentRequest(

        @Schema(description = "웹훅 이벤트 타입", example = "Transaction.Paid")
        String type,

        @Schema(description = "결제 데이터")
        WebhookData data

) {
    @Schema(description = "웹훅 결제 데이터")
    public record WebhookData(

            @Schema(description = "PortOne 결제 고유 ID", example = "portone_payment_id")
            String paymentId,

            @Schema(description = "서버 주문 ID", example = "PT-0QZHBKQN83C5Z")
            String orderId
    ) {}
}
