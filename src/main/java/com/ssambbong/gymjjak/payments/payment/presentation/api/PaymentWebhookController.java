package com.ssambbong.gymjjak.payments.payment.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import com.ssambbong.gymjjak.payments.payment.presentation.api.request.WebhookPaymentRequest;
import com.ssambbong.gymjjak.payments.payment.presentation.api.response.PaymentResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "결제")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentCommandUseCase paymentCommandUseCase;

    @Operation(summary = "PortOne 웹훅 수신", description = "PortOne에서 전송하는 결제 이벤트 웹훅을 수신하여 결제 상태를 갱신한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "웹훅 처리 완료",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/webhook")
    public ResponseEntity<GlobalApiResponse<Void>> receiveWebhook(
            @RequestBody WebhookPaymentRequest request
    ) {
        paymentCommandUseCase.processWebhook(
                new ProcessWebhookCommand(request.type(), request.data().paymentId(), request.data().orderId()));

        return ResponseEntity.ok(GlobalApiResponse.ok(PaymentResponseCode.WEBHOOK_RECEIVED));
    }
}
