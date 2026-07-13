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
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUseCase {

    private final PaymentRepository paymentRepository;
    private final PtCoursePaymentQueryPort ptCoursePaymentQueryPort;
    private final PortOnePaymentVerifyPort portOnePaymentVerifyPort;
    private final TransactionTemplate transactionTemplate;

    @Override
    @Transactional
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

        // "PT-" 접두사로 PT 결제 건 식별, TSID로 고유성 보장 (PortOne paymentId로 사용)
        String orderId = "PT-" + TsidCreator.getTsid().toString();

        paymentRepository.save(Payment.createForPt(command.userId(), command.ptCourseId(), orderId, info.price()));

        log.info("event=pt_payment_create_succeeded userId={} orderId={}", command.userId(), orderId);
        return new PaymentInitResult(orderId, info.price());
    }

    // 웹훅 수신
    // Transaction.Paid는 PortOne API를 트랜잭션 밖에서 먼저 호출하여 DB 커넥션 점유 시간을 최소화한다
    @Override
    public void processWebhook(ProcessWebhookCommand command) {
        log.info("event=webhook_received type={} orderId={}", command.type(), command.orderId());

        // PortOne API 호출 (트랜잭션 밖 — 외부 HTTP 호출이 DB 커넥션을 점유하지 않도록 분리)
        PortOnePaymentVerifyPort.PortOnePaymentInfo portOneInfo = null;
        if ("Transaction.Paid".equals(command.type())) {
            portOneInfo = portOnePaymentVerifyPort.getPaymentInfo(command.portonePaymentId());
        }

        final PortOnePaymentVerifyPort.PortOnePaymentInfo finalInfo = portOneInfo;

        // DB 조회 + 갱신만 트랜잭션으로 묶음
        transactionTemplate.execute(status -> {
            Payment payment = paymentRepository.findByOrderId(command.orderId())
                    .orElseThrow(PaymentNotFoundException::new);

            switch (command.type()) {
                case "Transaction.Paid" -> {
                    if (payment.getStatus() != PaymentStatus.PENDING) {
                        log.warn("event=webhook_skipped_duplicate type=Transaction.Paid orderId={} status={}",
                                command.orderId(), payment.getStatus());
                        return null;
                    }
                    // 검증 불일치는 상태 변경 없이 거절 — FAILED 확정 시 이후 정상 웹훅이 무시됨
                    if (!"PAID".equals(finalInfo.status())) {
                        log.warn("event=webhook_status_mismatch orderId={} portoneStatus={}",
                                command.orderId(), finalInfo.status());
                        return null;
                    }
                    if (finalInfo.amount() != payment.getAmount()) {
                        log.warn("event=webhook_amount_mismatch orderId={} expected={} actual={}",
                                command.orderId(), payment.getAmount(), finalInfo.amount());
                        return null;
                    }
                    paymentRepository.update(payment.pay(command.portonePaymentId()));
                    log.info("event=webhook_paid_succeeded orderId={}", command.orderId());
                }
                case "Transaction.Failed" -> {
                    if (payment.getStatus() != PaymentStatus.PENDING) {
                        log.warn("event=webhook_skipped_duplicate type=Transaction.Failed orderId={} status={}",
                                command.orderId(), payment.getStatus());
                        return null;
                    }
                    paymentRepository.update(payment.fail(null));
                    log.info("event=webhook_failed orderId={}", command.orderId());
                }
                case "Transaction.Cancelled" -> {
                    if (payment.getStatus() != PaymentStatus.PAID) {
                        log.warn("event=webhook_skipped_duplicate type=Transaction.Cancelled orderId={} status={}",
                                command.orderId(), payment.getStatus());
                        return null;
                    }
                    paymentRepository.update(payment.cancel());
                    log.info("event=webhook_cancelled orderId={}", command.orderId());
                }
                default -> log.warn("event=webhook_unknown_type type={}", command.type());
            }
            return null;
        });
    }
}
