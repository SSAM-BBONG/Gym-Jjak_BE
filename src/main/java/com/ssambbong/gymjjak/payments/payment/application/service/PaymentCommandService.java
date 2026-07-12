package com.ssambbong.gymjjak.payments.payment.application.service;

import com.github.f4b6a3.tsid.TsidCreator;
import com.ssambbong.gymjjak.payments.payment.application.command.CreatePtPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentDuplicateException;
import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUseCase {

    private final PaymentRepository paymentRepository;
    private final PtCoursePaymentQueryPort ptCoursePaymentQueryPort;

    @Override
    public PaymentInitResult createPtPayment(CreatePtPaymentCommand command) {
        log.debug("event=pt_payment_create userId={} ptCourseId={}", command.userId(), command.ptCourseId());

        // PT 중복 결제 검증
        if (paymentRepository.existsByUserIdAndPtCourseIdAndStatus(
                command.userId(), command.ptCourseId(), PaymentStatus.PAID)) {
            log.warn("event=pt_payment_create_failed reason=duplicate userId={} ptCourseId={}",
                    command.userId(), command.ptCourseId());
            throw new PaymentDuplicateException();
        }

        PtCoursePaymentQueryPort.PtCoursePaymentInfo info =
                ptCoursePaymentQueryPort.findPtCoursePaymentInfo(command.ptCourseId());

        // "PT-" 접두사로 PT 결제 건 식별, TSID로 고유성 보장 (PortOne merchant_uid로 사용)
        String orderId = "PT-" + TsidCreator.getTsid().toString();

        paymentRepository.save(Payment.createForPt(command.userId(), command.ptCourseId(), orderId, info.price()));

        log.info("event=pt_payment_create_succeeded userId={} orderId={}", command.userId(), orderId);
        return new PaymentInitResult(orderId, info.price());
    }
}
