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

            // PortOne V2에서 data.paymentId는 우리가 SDK에 전달한 merchantPaymentId (= 서버 orderId)
            @Schema(description = "서버 주문 ID (PortOne V2 data.paymentId)", example = "PT-0QZHBKQN83C5Z")
            String paymentId,

            @Schema(description = "PortOne 거래 고유 ID (PortOne V2 data.transactionId)", example = "portone-tx-0QZHBKQN83C5Z")
            String transactionId
    ) {}
}
