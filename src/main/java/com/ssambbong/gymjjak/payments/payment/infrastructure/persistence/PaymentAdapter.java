package com.ssambbong.gymjjak.payments.payment.infrastructure.persistence;

import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;
import com.ssambbong.gymjjak.payments.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository springDataPaymentRepository;

    // 결제 레코드 저장 (PENDING 상태로 생성)
    @Override
    public Payment save(Payment payment) {
        return springDataPaymentRepository.save(PaymentJpaEntity.from(payment)).toDomain();
    }

    // 웹훅 수신 시 orderId로 결제 건 조회
    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return springDataPaymentRepository.findByOrderId(orderId).map(PaymentJpaEntity::toDomain);
    }

    // 내 결제 내역 목록 조회 (최신순)
    @Override
    public List<Payment> findAllByUserIdOrderByIdDesc(Long userId) {
        return springDataPaymentRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream().map(PaymentJpaEntity::toDomain).toList();
    }

    // PT 코스 중복 결제 검증 (동일 유저 + 동일 코스 + PAID 존재 여부)
    @Override
    public boolean existsByUserIdAndPtCourseIdAndStatus(Long userId, Long ptCourseId, PaymentStatus status) {
        return springDataPaymentRepository.existsByUserIdAndPtCourseIdAndStatus(userId, ptCourseId, status);
    }

    // 구독 결제 PENDING 중복 검증
    @Override
    public boolean existsByUserIdAndProductTypeAndStatus(Long userId, ProductType productType, PaymentStatus status) {
        return springDataPaymentRepository.existsByUserIdAndProductTypeAndStatus(userId, productType, status);
    }

    // 웹훅 처리 후 결제 상태 갱신 (dirty checking으로 자동 UPDATE)
    @Override
    public void update(Payment payment) {
        springDataPaymentRepository.findById(payment.getId()).ifPresent(entity -> {
            switch (payment.getStatus()) {
                case PAID -> {
                    if (payment.getAiSubscriptionId() != null) {
                        entity.markPaid(payment.getPortonePaymentId(), payment.getAiSubscriptionId(), payment.getPaidAt());
                    } else {
                        entity.markPaid(payment.getPortonePaymentId(), payment.getPaidAt());
                    }
                }
                case CANCELLED -> entity.markCancelled(payment.getCancelledAt());
                case FAILED -> entity.markFailed(payment.getFailReason(), payment.getFailedAt());
                default -> {}
            }
        });
    }

    @Override
    public int expireStalePendingSubscriptions(Long userId, java.time.LocalDateTime threshold,
                                               java.time.LocalDateTime failedAt) {
        List<PaymentJpaEntity> stalePayments = springDataPaymentRepository
                .findAllByUserIdAndProductTypeAndStatusAndCreatedAtBefore(
                        userId, ProductType.SUBSCRIPTIONS, PaymentStatus.PENDING, threshold);
        stalePayments.forEach(payment -> payment.markFailed("PAYMENT_SESSION_EXPIRED", failedAt));
        return stalePayments.size();
    }
}
