package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.infrastructure.persistence.SpringDataPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PtReservationPaymentQueryAdapter
        implements com.ssambbong.gymjjak.pt.ptReservation.application.port.PaymentQueryPort,
                   com.ssambbong.gymjjak.pt.ptCourse.application.port.PaymentQueryPort {

    private final SpringDataPaymentRepository repository;

    // ptReservation / ptCourse → payments : 결제 완료 여부 조회
    @Override
    public boolean existsPaidByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return repository.existsByUserIdAndPtCourseIdAndStatus(userId, ptCourseId, PaymentStatus.PAID);
    }
}
