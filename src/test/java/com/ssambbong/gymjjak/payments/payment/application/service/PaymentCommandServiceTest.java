package com.ssambbong.gymjjak.payments.payment.application.service;

import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.CreateSubscriptionPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.port.PortOnePaymentVerifyPort;
import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionCreatePort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionLifecyclePort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionPaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionUserPort;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentNotFoundException;
import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;
import com.ssambbong.gymjjak.payments.payment.domain.repository.PaymentRepository;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PtCoursePaymentQueryPort ptCoursePaymentQueryPort;
    @Mock private PortOnePaymentVerifyPort portOnePaymentVerifyPort;
    @Mock private SubscriptionPaymentQueryPort subscriptionPaymentQueryPort;
    @Mock private SubscriptionCreatePort subscriptionCreatePort;
    @Mock private SubscriptionLifecyclePort subscriptionLifecyclePort;
    @Mock private SubscriptionUserPort subscriptionUserPort;
    @Mock private TransactionTemplate transactionTemplate;
    @Mock private Clock clock;

    @InjectMocks
    private PaymentCommandService paymentCommandService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-17T00:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Asia/Seoul"));
        // unknownType/alreadyPaid/orderNotFound 테스트는 execute() 호출 전에 return하므로 lenient 사용
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            org.springframework.transaction.support.TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(org.springframework.transaction.TransactionStatus.class));
        });
    }

    private static final String ORDER_ID = "PT-TEST0001";
    private static final String PORTONE_PAYMENT_ID = "portone-abc";
    private static final int AMOUNT = 300000;

    private Payment pendingPayment() {
        return Payment.restore(
                1L, 1L, 1L, null,
                ORDER_ID, null, AMOUNT,
                null, PaymentStatus.PENDING, ProductType.PT,
                null, null, null, null
        );
    }

    private Payment paidPayment() {
        return Payment.restore(
                1L, 1L, 1L, null,
                ORDER_ID, PORTONE_PAYMENT_ID, AMOUNT,
                null, PaymentStatus.PAID, ProductType.PT,
                null, null, null, null
        );
    }

    private Payment failedPayment() {
        return Payment.restore(
                1L, 1L, 1L, null,
                ORDER_ID, null, AMOUNT,
                null, PaymentStatus.FAILED, ProductType.PT,
                null, null, null, null
        );
    }

    private Payment cancelledPayment() {
        return Payment.restore(
                1L, 1L, 1L, null,
                ORDER_ID, PORTONE_PAYMENT_ID, AMOUNT,
                null, PaymentStatus.CANCELLED, ProductType.PT,
                null, null, null, null
        );
    }

    private Payment pendingSubscriptionPayment() {
        return Payment.restore(
                2L, 1L, null, null,
                "SUB-TEST0001", null, SubscriptionPlanType.MONTHLY.price(),
                SubscriptionPlanType.MONTHLY, PaymentStatus.PENDING, ProductType.SUBSCRIPTIONS,
                null, null, null, null
        );
    }

    private Payment paidSubscriptionPayment() {
        return Payment.restore(
                2L, 1L, null, 10L,
                "SUB-TEST0001", PORTONE_PAYMENT_ID, SubscriptionPlanType.MONTHLY.price(),
                SubscriptionPlanType.MONTHLY, PaymentStatus.PAID, ProductType.SUBSCRIPTIONS,
                null, null, null, null
        );
    }

    @Test
    @DisplayName("구독 결제 요청 시 사용자 행을 잠그고 PENDING 결제를 생성한다")
    void createSubscriptionPayment_locksUserAndCreatesPendingPayment() {
        when(subscriptionPaymentQueryPort.existsActiveByUserId(eq(1L), any())).thenReturn(false);
        when(paymentRepository.existsByUserIdAndProductTypeAndStatus(
                1L, ProductType.SUBSCRIPTIONS, PaymentStatus.PENDING)).thenReturn(false);

        paymentCommandService.createSubscriptionPayment(
                new CreateSubscriptionPaymentCommand(1L, SubscriptionPlanType.MONTHLY));

        verify(subscriptionUserPort).lockById(1L);
        verify(paymentRepository).expireStalePendingSubscriptions(eq(1L), any(), any());
        verify(paymentRepository).save(argThat(payment ->
                payment.getProductType() == ProductType.SUBSCRIPTIONS
                        && payment.getStatus() == PaymentStatus.PENDING
                        && payment.getPlanType() == SubscriptionPlanType.MONTHLY));
    }

    @Test
    @DisplayName("구독 결제 성공 시 구독을 생성하고 사용자를 유료 상태로 변경한다")
    void processWebhook_subscriptionPaid_createsSubscriptionAndMarksUserPaid() {
        Payment payment = pendingSubscriptionPayment();
        when(paymentRepository.findByOrderId("SUB-TEST0001")).thenReturn(Optional.of(payment));
        when(portOnePaymentVerifyPort.getPaymentInfo("SUB-TEST0001"))
                .thenReturn(new PortOnePaymentVerifyPort.PortOnePaymentInfo(
                        "PAID", SubscriptionPlanType.MONTHLY.price()));
        when(subscriptionCreatePort.create(eq(1L), eq(SubscriptionPlanType.MONTHLY),
                eq(SubscriptionPlanType.MONTHLY.price()), any(), any())).thenReturn(10L);

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Paid", "SUB-TEST0001", PORTONE_PAYMENT_ID));

        verify(subscriptionUserPort).lockById(1L);
        verify(subscriptionUserPort).markAsPaid(1L);
        verify(paymentRepository).update(argThat(updated ->
                updated.getStatus() == PaymentStatus.PAID
                        && Long.valueOf(10L).equals(updated.getAiSubscriptionId())));
    }

    @Test
    @DisplayName("구독 결제 환불 시 연결 구독을 만료시키고 사용자를 무료 상태로 변경한다")
    void processWebhook_subscriptionCancelled_expiresSubscriptionAndMarksUserUnpaid() {
        when(paymentRepository.findByOrderId("SUB-TEST0001"))
                .thenReturn(Optional.of(paidSubscriptionPayment()));
        when(subscriptionPaymentQueryPort.existsActiveByUserId(eq(1L), any())).thenReturn(false);

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Cancelled", "SUB-TEST0001", PORTONE_PAYMENT_ID));

        verify(subscriptionUserPort).lockById(1L);
        verify(subscriptionLifecyclePort).expire(10L);
        verify(subscriptionUserPort).markAsUnpaid(1L);
    }

    // ──── Transaction.Paid ────

    @Test
    @DisplayName("Transaction.Paid — 금액 일치 시 PAID로 전환된다")
    void processWebhook_paid_amountMatch_success() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));
        when(portOnePaymentVerifyPort.getPaymentInfo(ORDER_ID))
                .thenReturn(new PortOnePaymentVerifyPort.PortOnePaymentInfo("PAID", AMOUNT));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Paid", ORDER_ID, PORTONE_PAYMENT_ID));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).update(captor.capture());
        assertEquals(PaymentStatus.PAID, captor.getValue().getStatus());
        assertEquals(PORTONE_PAYMENT_ID, captor.getValue().getTransactionId());
    }

    @Test
    @DisplayName("Transaction.Paid — 금액 불일치 시 상태 변경 없이 무시된다")
    void processWebhook_paid_amountMismatch_ignored() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));
        when(portOnePaymentVerifyPort.getPaymentInfo(ORDER_ID))
                .thenReturn(new PortOnePaymentVerifyPort.PortOnePaymentInfo("PAID", 100));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Paid", ORDER_ID, PORTONE_PAYMENT_ID));

        verify(paymentRepository, never()).update(any());
    }

    @Test
    @DisplayName("Transaction.Paid — 이미 PAID 상태면 중복 처리하지 않는다")
    void processWebhook_paid_alreadyPaid_skipped() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(paidPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Paid", ORDER_ID, PORTONE_PAYMENT_ID));

        verify(paymentRepository, never()).update(any());
        verify(portOnePaymentVerifyPort, never()).getPaymentInfo(any());
    }

    // ──── Transaction.Failed ────

    @Test
    @DisplayName("Transaction.Failed — PENDING 상태면 FAILED로 전환된다")
    void processWebhook_failed_success() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Failed", ORDER_ID, PORTONE_PAYMENT_ID));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).update(captor.capture());
        assertEquals(PaymentStatus.FAILED, captor.getValue().getStatus());
        assertNull(captor.getValue().getFailReason());
    }

    @Test
    @DisplayName("Transaction.Failed — 이미 FAILED 상태면 중복 처리하지 않는다")
    void processWebhook_failed_alreadyFailed_skipped() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(failedPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Failed", ORDER_ID, PORTONE_PAYMENT_ID));

        verify(paymentRepository, never()).update(any());
    }

    // ──── Transaction.Cancelled ────

    @Test
    @DisplayName("Transaction.Cancelled — PAID 상태면 CANCELLED로 전환된다")
    void processWebhook_cancelled_success() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(paidPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Cancelled", ORDER_ID, PORTONE_PAYMENT_ID));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).update(captor.capture());
        assertEquals(PaymentStatus.CANCELLED, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Transaction.Cancelled — 이미 CANCELLED 상태면 중복 처리하지 않는다")
    void processWebhook_cancelled_alreadyCancelled_skipped() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(cancelledPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Cancelled", ORDER_ID, PORTONE_PAYMENT_ID));

        verify(paymentRepository, never()).update(any());
    }

    @Test
    @DisplayName("Transaction.Cancelled — PENDING 상태면 중복 처리하지 않는다")
    void processWebhook_cancelled_pendingStatus_skipped() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Cancelled", ORDER_ID, PORTONE_PAYMENT_ID));

        verify(paymentRepository, never()).update(any());
    }

    // ──── 예외 / unknown ────

    @Test
    @DisplayName("존재하지 않는 orderId면 PaymentNotFoundException이 발생한다")
    void processWebhook_orderNotFound_throwsException() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> paymentCommandService.processWebhook(
                        new ProcessWebhookCommand("Transaction.Paid", ORDER_ID, PORTONE_PAYMENT_ID)));

        verify(paymentRepository, never()).update(any());
    }

    @Test
    @DisplayName("알 수 없는 웹훅 타입은 무시된다")
    void processWebhook_unknownType_ignored() {
        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Unknown", ORDER_ID, PORTONE_PAYMENT_ID));

        verify(paymentRepository, never()).findByOrderId(any());
        verify(paymentRepository, never()).update(any());
    }
}
