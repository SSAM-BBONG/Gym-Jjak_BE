package com.ssambbong.gymjjak.payments.payment.domain.repository;

import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    // 결제 레코드 저장 (PENDING 상태로 생성)
    Payment save(Payment payment);

    // 웹훅 수신 시 orderId로 결제 건 조회
    Optional<Payment> findByOrderId(String orderId);

    // 내 결제 내역 목록 조회 (최신순)
    List<Payment> findAllByUserIdOrderByIdDesc(Long userId);

    // PT 코스 중복 결제 검증 (동일 유저 + 동일 코스 + PAID 존재 여부)
    boolean existsByUserIdAndPtCourseIdAndStatus(Long userId, Long ptCourseId, PaymentStatus status);

    // 웹훅 처리 후 결제 상태 갱신 (PAID / CANCELLED / FAILED)
    void update(Payment payment);
}
