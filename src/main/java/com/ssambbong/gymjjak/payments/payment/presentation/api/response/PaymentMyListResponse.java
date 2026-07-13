package com.ssambbong.gymjjak.payments.payment.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentQueryUseCase;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "내 결제 내역 목록 응답")
public record PaymentMyListResponse(

        @Schema(description = "결제 내역 목록 (최신순)")
        List<PaymentItem> payments

) {
    public static PaymentMyListResponse from(List<PaymentQueryUseCase.PaymentListView> views) {
        return new PaymentMyListResponse(views.stream().map(PaymentItem::from).toList());
    }

    @Schema(description = "결제 내역 항목")
    public record PaymentItem(

            @Schema(description = "상품 유형", example = "PT", allowableValues = {"PT", "SUBSCRIPTIONS"})
            ProductType productType,

            @Schema(description = "PT면 코스명, 구독이면 플랜 타입", example = "가슴 집중 PT 3회")
            String itemName,

            @Schema(description = "결제 금액", example = "150000")
            int amount,

            @Schema(description = "결제 상태", example = "PAID", allowableValues = {"PENDING", "PAID", "CANCELLED", "FAILED"})
            PaymentStatus status,

            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @Schema(description = "상태별 처리 시각. PAID→결제완료일, CANCELLED→취소일, FAILED→실패일, PENDING→null", example = "2026-07-12T21:53:12", nullable = true)
            LocalDateTime processedAt

    ) {
        public static PaymentItem from(PaymentQueryUseCase.PaymentListView view) {
            return new PaymentItem(
                    view.productType(),
                    view.itemName(),
                    view.amount(),
                    view.status(),
                    view.processedAt()
            );
        }
    }
}
