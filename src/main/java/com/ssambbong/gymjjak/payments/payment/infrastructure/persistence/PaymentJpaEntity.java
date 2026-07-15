package com.ssambbong.gymjjak.payments.payment.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "payments")
public class PaymentJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pt_course_id")
    private Long ptCourseId;

    @Column(name = "ai_subscription_id")
    private Long aiSubscriptionId;

    @Column(name = "order_id", nullable = false, length = 50)
    private String orderId;

    @Column(name = "portone_payment_id", length = 50)
    private String portonePaymentId;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 20)
    private ProductType productType;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "fail_reason", length = 255)
    private String failReason;

    public PaymentJpaEntity(
            Long userId,
            Long ptCourseId,
            Long aiSubscriptionId,
            String orderId,
            int amount,
            ProductType productType
    ) {
        this.userId = userId;
        this.ptCourseId = ptCourseId;
        this.aiSubscriptionId = aiSubscriptionId;
        this.orderId = orderId;
        this.amount = amount;
        this.productType = productType;
        this.status = PaymentStatus.PENDING;
    }

    public void markPaid(String portonePaymentId) {
        this.portonePaymentId = portonePaymentId;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markPaid(String portonePaymentId, Long subscriptionId) {
        this.portonePaymentId = portonePaymentId;
        this.aiSubscriptionId = subscriptionId;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markFailed(String failReason) {
        this.status = PaymentStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.failReason = failReason;
    }

    public static PaymentJpaEntity from(Payment domain) {
        return new PaymentJpaEntity(
                domain.getUserId(),
                domain.getPtCourseId(),
                domain.getAiSubscriptionId(),
                domain.getOrderId(),
                domain.getAmount(),
                domain.getProductType()
        );
    }

    public Payment toDomain() {
        return Payment.restore(
                id, userId, ptCourseId, aiSubscriptionId,
                orderId, portonePaymentId, amount,
                status, productType,
                paidAt, cancelledAt, failedAt, failReason
        );
    }
}
