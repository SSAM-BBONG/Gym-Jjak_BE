package com.ssambbong.gymjjak.payments.payment.application.service;

import com.github.f4b6a3.tsid.TsidCreator;
import com.ssambbong.gymjjak.payments.payment.application.command.CreatePtPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.CreateSubscriptionPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;
import com.ssambbong.gymjjak.payments.payment.application.port.PortOnePaymentVerifyPort;
import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionCreatePort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionPaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentDuplicateException;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentNotFoundException;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentTargetNotFoundException;
import com.ssambbong.gymjjak.payments.payment.domain.exception.SubscriptionDuplicateException;
import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.repository.PaymentRepository;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUseCase {

    private static final Set<String> SUPPORTED_WEBHOOK_TYPES =
            Set.of("Transaction.Paid", "Transaction.Failed", "Transaction.Cancelled");

    private final PaymentRepository paymentRepository;
    private final PtCoursePaymentQueryPort ptCoursePaymentQueryPort;
    private final SubscriptionPaymentQueryPort subscriptionPaymentQueryPort;
    private final SubscriptionCreatePort subscriptionCreatePort;
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
                ptCoursePaymentQueryPort.findPtCoursePaymentInfo(command.ptCourseId())
                        .orElseThrow(PaymentTargetNotFoundException::new);

        // "PT-" 접두사로 PT 결제 건 식별, TSID로 고유성 보장 (PortOne paymentId로 사용)
        String orderId = "PT-" + TsidCreator.getTsid().toString();

        paymentRepository.save(Payment.createForPt(command.userId(), command.ptCourseId(), orderId, info.price()));

        log.info("event=pt_payment_create_succeeded userId={} orderId={}", command.userId(), orderId);
        return new PaymentInitResult(orderId, info.price());
    }

    @Override
    @Transactional
    public PaymentInitResult createSubscriptionPayment(CreateSubscriptionPaymentCommand command) {
        log.debug("event=subscription_payment_create userId={} planType={}", command.userId(), command.planType());

        // 활성 구독 중복 검증 (ACTIVE 구독 또는 PENDING 구독 결제 존재 시 차단)
        if (subscriptionPaymentQueryPort.existsActiveByUserId(command.userId())
                || paymentRepository.existsByUserIdAndProductTypeAndStatus(
                        command.userId(), ProductType.SUBSCRIPTIONS, PaymentStatus.PENDING)) {
            log.warn("event=subscription_payment_create_failed reason=duplicate userId={}", command.userId());
            throw new SubscriptionDuplicateException();
        }

        int amount = command.planType().price();

        String orderId = "SUB-" + TsidCreator.getTsid().toString();

        paymentRepository.save(Payment.createForSubscription(command.userId(), orderId, amount, command.planType()));

        log.info("event=subscription_payment_create_succeeded userId={} orderId={} planType={}",
                command.userId(), orderId, command.planType());
        return new PaymentInitResult(orderId, amount);
    }

    // 웹훅 수신
    // Transaction.Paid는 중복 체크 후 PortOne API 호출, DB 갱신만 짧은 트랜잭션으로 처리
    @Override
    public void processWebhook(ProcessWebhookCommand command) {
        log.info("event=webhook_received type={} orderId={}", command.type(), command.orderId());

        // 지원하지 않는 타입은 DB 조회 없이 즉시 무시
        if (!SUPPORTED_WEBHOOK_TYPES.contains(command.type())) {
            log.warn("event=webhook_unknown_type type={}", command.type());
            return;
        }

        // Transaction.Paid: 이미 처리된 결제는 PortOne 외부 호출 없이 종료
        PortOnePaymentVerifyPort.PortOnePaymentInfo portOneInfo = null;
        if ("Transaction.Paid".equals(command.type())) {
            Payment preCheck = paymentRepository.findByOrderId(command.orderId())
                    .orElseThrow(PaymentNotFoundException::new);
            if (preCheck.getStatus() != PaymentStatus.PENDING) {
                log.warn("event=webhook_skipped_duplicate type=Transaction.Paid orderId={} status={}",
                        command.orderId(), preCheck.getStatus());
                return;
            }
            // PENDING 확인 후에만 PortOne API 호출 (트랜잭션 밖 — DB 커넥션 점유 방지)
            portOneInfo = portOnePaymentVerifyPort.getPaymentInfo(command.portonePaymentId());
        }

        final PortOnePaymentVerifyPort.PortOnePaymentInfo finalInfo = portOneInfo;

        // DB 조회 + 갱신만 트랜잭션으로 묶음
        transactionTemplate.execute(status -> {
            Payment payment = paymentRepository.findByOrderId(command.orderId())
                    .orElseThrow(PaymentNotFoundException::new);

            switch (command.type()) {
                case "Transaction.Paid" -> {
                    // pre-check 이후 상태 변경 방어
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
                    if (payment.getProductType() == ProductType.SUBSCRIPTIONS) {
                        SubscriptionPlanType planType = payment.getPlanType();
                        LocalDateTime startedAt = LocalDateTime.now();
                        LocalDateTime expiredAt = planType.expiresAt(startedAt);
                        Long subscriptionId = subscriptionCreatePort.create(
                                payment.getUserId(), planType, payment.getAmount(), startedAt, expiredAt);
                        paymentRepository.update(payment.paySubscription(command.portonePaymentId(), subscriptionId));
                        log.info("event=webhook_subscription_created orderId={} subscriptionId={}", command.orderId(), subscriptionId);
                    } else {
                        paymentRepository.update(payment.pay(command.portonePaymentId()));
                    }
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
            }
            return null;
        });
    }
}
