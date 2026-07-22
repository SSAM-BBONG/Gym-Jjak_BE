package com.ssambbong.gymjjak.payments.payment.application.service;

import com.github.f4b6a3.tsid.TsidCreator;
import com.ssambbong.gymjjak.payments.payment.application.command.CreatePtPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.CreateSubscriptionPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;
import com.ssambbong.gymjjak.payments.payment.application.port.PortOnePaymentVerifyPort;
import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionCreatePort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionPaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionLifecyclePort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionUserPort;
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
import java.time.Clock;
import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUseCase {

    private static final Set<String> SUPPORTED_WEBHOOK_TYPES =
            Set.of("Transaction.Paid", "Transaction.Failed", "Transaction.Cancelled");
    private static final Duration PENDING_PAYMENT_TTL = Duration.ofMinutes(30);

    private final PaymentRepository paymentRepository;
    private final PtCoursePaymentQueryPort ptCoursePaymentQueryPort;
    private final SubscriptionPaymentQueryPort subscriptionPaymentQueryPort;
    private final SubscriptionCreatePort subscriptionCreatePort;
    private final SubscriptionLifecyclePort subscriptionLifecyclePort;
    private final SubscriptionUserPort subscriptionUserPort;
    private final PortOnePaymentVerifyPort portOnePaymentVerifyPort;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

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

        // 같은 사용자의 동시 결제 요청은 사용자 행 잠금으로 직렬화한다.
        subscriptionUserPort.lockById(command.userId());
        LocalDateTime now = LocalDateTime.now(clock);

        // 결제창 이탈로 남은 오래된 PENDING 결제는 실패 처리한다.
        paymentRepository.expireStalePendingSubscriptions(
                command.userId(), now.minus(PENDING_PAYMENT_TTL), now);

        // 활성 구독 중복 검증 (ACTIVE 구독 또는 PENDING 구독 결제 존재 시 차단)
        if (subscriptionPaymentQueryPort.existsActiveByUserId(command.userId(), now)
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

        Payment initialPayment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(PaymentNotFoundException::new);

        // Transaction.Paid: 이미 처리된 결제는 PortOne 외부 호출 없이 종료
        PortOnePaymentVerifyPort.PortOnePaymentInfo portOneInfo = null;
        if ("Transaction.Paid".equals(command.type())) {
            if (initialPayment.getStatus() != PaymentStatus.PENDING) {
                log.warn("event=webhook_skipped_duplicate type=Transaction.Paid orderId={} status={}",
                        command.orderId(), initialPayment.getStatus());
                return;
            }
            // PENDING 확인 후에만 PortOne API 호출 (트랜잭션 밖 — DB 커넥션 점유 방지)
            // PortOne 쪽 결제 건 조회이므로 우리 orderId가 아니라 PortOne이 발급한 transactionId를 사용한다.
            portOneInfo = portOnePaymentVerifyPort.getPaymentInfo(command.transactionId());
        }

        final PortOnePaymentVerifyPort.PortOnePaymentInfo finalInfo = portOneInfo;

        // DB 조회 + 갱신만 트랜잭션으로 묶음
        transactionTemplate.execute(status -> {
            if (initialPayment.getProductType() == ProductType.SUBSCRIPTIONS) {
                // 구독 웹훅은 사용자 행을 먼저 잠근 뒤 결제 상태를 다시 읽는다.
                subscriptionUserPort.lockById(initialPayment.getUserId());
            }
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
                        // 구독 생성과 사용자 권한 변경을 동일한 사용자 잠금 안에서 처리한다.
                        SubscriptionPlanType planType = payment.getPlanType();
                        LocalDateTime startedAt = LocalDateTime.now(clock);
                        LocalDateTime expiredAt = planType.expiresAt(startedAt);
                        Long subscriptionId = subscriptionCreatePort.create(
                                payment.getUserId(), planType, payment.getAmount(), startedAt, expiredAt);
                        paymentRepository.update(payment.paySubscription(
                                command.transactionId(), subscriptionId, startedAt));
                        subscriptionUserPort.markAsPaid(payment.getUserId());
                        log.info("event=webhook_subscription_created orderId={} subscriptionId={}", command.orderId(), subscriptionId);
                    } else {
                        paymentRepository.update(payment.pay(command.transactionId(), LocalDateTime.now(clock)));
                    }
                    log.info("event=webhook_paid_succeeded orderId={}", command.orderId());
                }
                case "Transaction.Failed" -> {
                    if (payment.getStatus() != PaymentStatus.PENDING) {
                        log.warn("event=webhook_skipped_duplicate type=Transaction.Failed orderId={} status={}",
                                command.orderId(), payment.getStatus());
                        return null;
                    }
                    paymentRepository.update(payment.fail(null, LocalDateTime.now(clock)));
                    log.info("event=webhook_failed orderId={}", command.orderId());
                }
                case "Transaction.Cancelled" -> {
                    if (payment.getStatus() != PaymentStatus.PAID) {
                        log.warn("event=webhook_skipped_duplicate type=Transaction.Cancelled orderId={} status={}",
                                command.orderId(), payment.getStatus());
                        return null;
                    }
                    LocalDateTime cancelledAt = LocalDateTime.now(clock);
                    if (payment.getProductType() == ProductType.SUBSCRIPTIONS
                            && payment.getAiSubscriptionId() != null) {
                        // 환불된 구독은 별도 CANCELLED 상태 없이 EXPIRED로 종료한다.
                        subscriptionLifecyclePort.expire(payment.getAiSubscriptionId());
                        paymentRepository.update(payment.cancel(cancelledAt));
                        if (!subscriptionPaymentQueryPort.existsActiveByUserId(payment.getUserId(), cancelledAt)) {
                            subscriptionUserPort.markAsUnpaid(payment.getUserId());
                        }
                    } else {
                        paymentRepository.update(payment.cancel(cancelledAt));
                    }
                    log.info("event=webhook_cancelled orderId={}", command.orderId());
                }
            }
            return null;
        });
    }
}
