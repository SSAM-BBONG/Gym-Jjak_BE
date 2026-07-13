package com.ssambbong.gymjjak.payments.payment.application.service;

import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionPaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentQueryUseCase;
import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.repository.PaymentRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentQueryService implements PaymentQueryUseCase {

    private final PaymentRepository paymentRepository;
    private final PtCoursePaymentQueryPort ptCoursePaymentQueryPort;
    private final SubscriptionPaymentQueryPort subscriptionPaymentQueryPort;

    // PT 코스 구매 여부 조회 (PAID 상태 결제 존재 시 true)
    @Override
    public boolean isPtCoursePurchased(Long userId, Long ptCourseId) {
        return paymentRepository.existsByUserIdAndPtCourseIdAndStatus(userId, ptCourseId, PaymentStatus.PAID);
    }

    // 내 결제 내역 목록 조회 (최신순)
    @Override
    public List<PaymentListView> findMyPayments(Long userId) {
        return paymentRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(this::toListView)
                .toList();
    }

    // Payment 도메인 → 응답 뷰 변환
    private PaymentListView toListView(Payment payment) {
        return new PaymentListView(
                payment.getProductType(),
                resolveItemName(payment),
                payment.getAmount(),
                payment.getStatus(),
                resolveProcessedAt(payment)
        );
    }

    // PT면 코스명, 구독이면 플랜 타입 (MONTHLY / YEARLY)
    // PT 코스가 삭제된 경우 한 건 실패로 전체 목록이 깨지지 않도록 UNKNOWN으로 fallback
    private String resolveItemName(Payment payment) {
        return switch (payment.getProductType()) {
            case PT -> {
                try {
                    yield ptCoursePaymentQueryPort.findPtCoursePaymentInfo(payment.getPtCourseId()).title();
                } catch (PtCourseNotFoundException e) {
                    log.warn("event=pt_course_not_found ptCourseId={}", payment.getPtCourseId());
                    yield "UNKNOWN";
                }
            }
            case SUBSCRIPTIONS -> subscriptionPaymentQueryPort
                    .findPlanTypeName(payment.getAiSubscriptionId())
                    .orElse("UNKNOWN");
        };
    }

    // 상태별 처리 시각: PAID→결제완료일, CANCELLED→취소일, FAILED→실패일, PENDING→null
    private LocalDateTime resolveProcessedAt(Payment payment) {
        return switch (payment.getStatus()) {
            case PAID -> payment.getPaidAt();
            case CANCELLED -> payment.getCancelledAt();
            case FAILED -> payment.getFailedAt();
            case PENDING -> null;
        };
    }
}
