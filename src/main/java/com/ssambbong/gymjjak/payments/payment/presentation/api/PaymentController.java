package com.ssambbong.gymjjak.payments.payment.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.payments.payment.application.command.CreatePtPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentQueryUseCase;
import com.ssambbong.gymjjak.payments.payment.presentation.api.request.CreatePtPaymentRequest;
import com.ssambbong.gymjjak.payments.payment.presentation.api.response.CreatePtPaymentResponse;
import com.ssambbong.gymjjak.payments.payment.presentation.api.response.PaymentMyListResponse;
import com.ssambbong.gymjjak.payments.payment.presentation.api.response.PaymentResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "결제", description = "결제 관련 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCommandUseCase paymentCommandUseCase;
    private final PaymentQueryUseCase paymentQueryUseCase;

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "내 결제 내역 목록 조회", description = "본인의 결제 내역을 최신순으로 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentMyListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<?>> getMyPayments(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(GlobalApiResponse.ok(PaymentResponseCode.PAYMENTS_FETCHED,
                PaymentMyListResponse.from(paymentQueryUseCase.findMyPayments(authUser.userId()))));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "PT 결제 요청", description = "PT 코스 결제를 시작한다. orderId와 금액을 반환하며, 프론트에서 PortOne SDK 호출 시 사용한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "결제 요청 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreatePtPaymentResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음 (USER, TRAINER만 접근 가능)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "PT 코스를 찾을 수 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "이미 구매한 PT 코스",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/pt")
    public ResponseEntity<GlobalApiResponse<?>> createPtPayment(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreatePtPaymentRequest request
    ) {
        PaymentCommandUseCase.PaymentInitResult result = paymentCommandUseCase.createPtPayment(
                new CreatePtPaymentCommand(authUser.userId(), request.ptCourseId()));

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(PaymentResponseCode.PAYMENT_PT_CREATED,
                        CreatePtPaymentResponse.from(result)));
    }
}
