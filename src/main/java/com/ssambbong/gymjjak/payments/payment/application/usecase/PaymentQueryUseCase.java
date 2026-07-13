package com.ssambbong.gymjjak.payments.payment.application.usecase;

import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentQueryUseCase {

    // 내 결제 내역 목록 조회 (최신순)
    List<PaymentListView> findMyPayments(Long userId);

    // PT 코스 구매 여부 조회 (PAID 상태 결제 존재 시 true)
    boolean isPtCoursePurchased(Long userId, Long ptCourseId);

    record PaymentListView(
            ProductType productType,
            String itemName,
            int amount,
            PaymentStatus status,
            LocalDateTime processedAt
    ) {}
}
