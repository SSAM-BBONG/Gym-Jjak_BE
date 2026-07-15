package com.ssambbong.gymjjak.payments.payment.presentation.api.response;

import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "구독 결제 요청 응답")
public record CreateSubscriptionPaymentResponse(
        @Schema(description = "서버 생성 주문번호. PortOne V2 SDK 호출 시 paymentId로 사용", example = "SUB-05YJQK1Z0GY4R")
        String orderId,

        @Schema(description = "결제 금액. PortOne SDK 호출 시 amount로 사용", example = "4900")
        int amount
) {
    public static CreateSubscriptionPaymentResponse from(PaymentCommandUseCase.PaymentInitResult result) {
        return new CreateSubscriptionPaymentResponse(result.orderId(), result.amount());
    }
}
