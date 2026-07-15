package com.ssambbong.gymjjak.payments.payment.domain.model;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Payment {

    private final Long id;
    private final Long userId;
    private final Long ptCourseId;
    private final Long aiSubscriptionId;
    private final String orderId;
    private final String portonePaymentId;
    private final int amount;
    private final SubscriptionPlanType planType;
    private final PaymentStatus status;
    private final ProductType productType;
    private final LocalDateTime paidAt;
    private final LocalDateTime cancelledAt;
    private final LocalDateTime failedAt;
    private final String failReason;

    private Payment(
            Long id, Long userId, Long ptCourseId, Long aiSubscriptionId,
            String orderId, String portonePaymentId, int amount,
            SubscriptionPlanType planType, PaymentStatus status, ProductType productType,
            LocalDateTime paidAt, LocalDateTime cancelledAt,
            LocalDateTime failedAt, String failReason
    ) {
        this.id = id;
        this.userId = userId;
        this.ptCourseId = ptCourseId;
        this.aiSubscriptionId = aiSubscriptionId;
        this.orderId = orderId;
        this.portonePaymentId = portonePaymentId;
        this.amount = amount;
        this.planType = planType;
        this.status = status;
        this.productType = productType;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
        this.failedAt = failedAt;
        this.failReason = failReason;
    }

    public static Payment createForPt(Long userId, Long ptCourseId, String orderId, int amount) {
        return new Payment(null, userId, ptCourseId, null, orderId, null,
                amount, null, PaymentStatus.PENDING, ProductType.PT,
                null, null, null, null);
    }

    public static Payment createForSubscription(Long userId, String orderId, int amount, SubscriptionPlanType planType) {
        return new Payment(null, userId, null, null, orderId, null,
                amount, planType, PaymentStatus.PENDING, ProductType.SUBSCRIPTIONS,
                null, null, null, null);
    }

    public static Payment restore(
            Long id, Long userId, Long ptCourseId, Long aiSubscriptionId,
            String orderId, String portonePaymentId, int amount,
            SubscriptionPlanType planType, PaymentStatus status, ProductType productType,
            LocalDateTime paidAt, LocalDateTime cancelledAt,
            LocalDateTime failedAt, String failReason
    ) {
        return new Payment(id, userId, ptCourseId, aiSubscriptionId,
                orderId, portonePaymentId, amount, planType, status, productType,
                paidAt, cancelledAt, failedAt, failReason);
    }

    // PortOne 결제 확인 후 PAID로 전환 (PT)
    public Payment pay(String portonePaymentId) {
        return new Payment(id, userId, ptCourseId, aiSubscriptionId,
                orderId, portonePaymentId, amount,
                planType, PaymentStatus.PAID, productType,
                LocalDateTime.now(), null, null, null);
    }

    // PortOne 결제 확인 후 PAID로 전환 + 생성된 구독 ID 연결 (구독)
    public Payment paySubscription(String portonePaymentId, Long subscriptionId) {
        return new Payment(id, userId, ptCourseId, subscriptionId,
                orderId, portonePaymentId, amount,
                planType, PaymentStatus.PAID, productType,
                LocalDateTime.now(), null, null, null);
    }

    // 결제 실패 처리
    public Payment fail(String failReason) {
        return new Payment(id, userId, ptCourseId, aiSubscriptionId,
                orderId, portonePaymentId, amount,
                planType, PaymentStatus.FAILED, productType,
                null, null, LocalDateTime.now(), failReason);
    }

    // 결제 취소 처리
    public Payment cancel() {
        return new Payment(id, userId, ptCourseId, aiSubscriptionId,
                orderId, portonePaymentId, amount,
                planType, PaymentStatus.CANCELLED, productType,
                paidAt, LocalDateTime.now(), null, null);
    }
}
