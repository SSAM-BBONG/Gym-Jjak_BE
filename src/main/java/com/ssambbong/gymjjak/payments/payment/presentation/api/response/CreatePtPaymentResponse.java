package com.ssambbong.gymjjak.payments.payment.presentation.api.response;

import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

// PT 결제 요청 응답 DTO
// 프론트에서 PortOne SDK 호출 시 orderId와 amount를 그대로 사용한다
@Schema(description = "PT 결제 요청 응답")
public record CreatePtPaymentResponse(
        @Schema(description = "서버 생성 주문번호. PortOne SDK 호출 시 merchant_uid로 사용", example = "PT-05YJQK1Z0GY4R")
        String orderId,

        @Schema(description = "결제 금액. PortOne SDK 호출 시 amount로 사용", example = "150000")
        int amount
) {
    public static CreatePtPaymentResponse from(PaymentCommandUseCase.PaymentInitResult result) {
        return new CreatePtPaymentResponse(result.orderId(), result.amount());
    }
}
