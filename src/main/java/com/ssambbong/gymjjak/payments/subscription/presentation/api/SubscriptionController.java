package com.ssambbong.gymjjak.payments.subscription.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.payments.subscription.application.usecase.SubscriptionQueryUseCase;
import com.ssambbong.gymjjak.payments.subscription.presentation.api.response.SubscriptionMeResponse;
import com.ssambbong.gymjjak.payments.subscription.presentation.api.response.SubscriptionPlansResponse;
import com.ssambbong.gymjjak.payments.subscription.presentation.api.response.SubscriptionResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "구독", description = "구독 관련 API")
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionQueryUseCase subscriptionQueryUseCase;

    // 구독 플랜 목록 조회
    @Operation(summary = "구독 플랜 목록 조회", description = "이용 가능한 구독 플랜과 가격을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SubscriptionPlansResponse.class)))
    })
    @GetMapping("/plans")
    public ResponseEntity<GlobalApiResponse<?>> getPlans() {
        return ResponseEntity.ok(GlobalApiResponse.ok(
                SubscriptionResponseCode.SUBSCRIPTION_PLANS_FETCHED,
                SubscriptionPlansResponse.from(subscriptionQueryUseCase.findPlans())
        ));
    }

    // 내 구독 조회
    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "내 구독 조회", description = "본인의 활성 구독 정보를 조회한다. 활성 구독이 없으면 data는 null.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (활성 구독 없으면 data: null)",
                    content = @Content(schema = @Schema(implementation = SubscriptionMeResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<?>> getMySubscription(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(GlobalApiResponse.ok(
                SubscriptionResponseCode.SUBSCRIPTION_FETCHED,
                subscriptionQueryUseCase.findMySubscription(authUser.userId())
                        .map(SubscriptionMeResponse::from)
                        .orElse(null)
        ));
    }
}