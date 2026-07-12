package com.ssambbong.gymjjak.payments.payment.application.service;

import com.github.f4b6a3.tsid.TsidCreator;
import com.ssambbong.gymjjak.payments.payment.application.command.CreatePtPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;
import com.ssambbong.gymjjak.payments.payment.application.port.PortOnePaymentVerifyPort;
import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentDuplicateException;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentNotFoundException;
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
    private final PortOnePaymentVerifyPort portOnePaymentVerifyPort;

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

    // 웹훅 수신
    @Override
    public void processWebhook(ProcessWebhookCommand command) {
        log.info("event=webhook_received type={} orderId={}", command.type(), command.orderId());

        Payment payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(PaymentNotFoundException::new);

        switch (command.type()) {
            case "Transaction.Paid" -> {
                if (payment.getStatus() != PaymentStatus.PENDING) {
                    log.warn("event=webhook_skipped_duplicate type=Transaction.Paid orderId={} status={}",
                            command.orderId(), payment.getStatus());
                    return;
                }
                // PortOne API로 실제 결제 확인 (금액 위변조 방지)
                PortOnePaymentVerifyPort.PortOnePaymentInfo info =
                        portOnePaymentVerifyPort.getPaymentInfo(command.portonePaymentId());
                if (info.amount() != payment.getAmount()) {
                    log.warn("event=webhook_amount_mismatch orderId={} expected={} actual={}",
                            command.orderId(), payment.getAmount(), info.amount());
                    paymentRepository.update(payment.fail("결제 금액 불일치"));
                    return;
                }
                paymentRepository.update(payment.pay(command.portonePaymentId()));
                log.info("event=webhook_paid_succeeded orderId={}", command.orderId());
            }
            case "Transaction.Failed" -> {
                if (payment.getStatus() != PaymentStatus.PENDING) {
                    log.warn("event=webhook_skipped_duplicate type=Transaction.Failed orderId={} status={}",
                            command.orderId(), payment.getStatus());
                    return;
                }
                paymentRepository.update(payment.fail(null));
                log.info("event=webhook_failed orderId={}", command.orderId());
            }
            case "Transaction.Cancelled" -> {
                if (payment.getStatus() != PaymentStatus.PAID) {
                    log.warn("event=webhook_skipped_duplicate type=Transaction.Cancelled orderId={} status={}",
                            command.orderId(), payment.getStatus());
                    return;
                }
                paymentRepository.update(payment.cancel());
                log.info("event=webhook_cancelled orderId={}", command.orderId());
            }
            default -> log.warn("event=webhook_unknown_type type={}", command.type());
        }
    }
}
