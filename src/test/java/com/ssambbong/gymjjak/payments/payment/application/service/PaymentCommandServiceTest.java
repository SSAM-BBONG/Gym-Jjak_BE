package com.ssambbong.gymjjak.payments.payment.application.service;

import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;
import com.ssambbong.gymjjak.payments.payment.application.port.PortOnePaymentVerifyPort;
import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentNotFoundException;
import com.ssambbong.gymjjak.payments.payment.domain.model.Payment;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;
import com.ssambbong.gymjjak.payments.payment.domain.repository.PaymentRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PtCoursePaymentQueryPort ptCoursePaymentQueryPort;
    @Mock private PortOnePaymentVerifyPort portOnePaymentVerifyPort;
    @Mock private TransactionTemplate transactionTemplate;

    @InjectMocks
    private PaymentCommandService paymentCommandService;

    @BeforeEach
    void setUp() {
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
                PaymentStatus.PENDING, ProductType.PT,
                null, null, null, null
        );
    }

    private Payment paidPayment() {
        return Payment.restore(
                1L, 1L, 1L, null,
                ORDER_ID, PORTONE_PAYMENT_ID, AMOUNT,
                PaymentStatus.PAID, ProductType.PT,
                null, null, null, null
        );
    }

    private Payment failedPayment() {
        return Payment.restore(
                1L, 1L, 1L, null,
                ORDER_ID, null, AMOUNT,
                PaymentStatus.FAILED, ProductType.PT,
                null, null, null, null
        );
    }

    private Payment cancelledPayment() {
        return Payment.restore(
                1L, 1L, 1L, null,
                ORDER_ID, PORTONE_PAYMENT_ID, AMOUNT,
                PaymentStatus.CANCELLED, ProductType.PT,
                null, null, null, null
        );
    }

    // ──── Transaction.Paid ────

    @Test
    @DisplayName("Transaction.Paid — 금액 일치 시 PAID로 전환된다")
    void processWebhook_paid_amountMatch_success() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));
        when(portOnePaymentVerifyPort.getPaymentInfo(PORTONE_PAYMENT_ID))
                .thenReturn(new PortOnePaymentVerifyPort.PortOnePaymentInfo("PAID", AMOUNT));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Paid", PORTONE_PAYMENT_ID, ORDER_ID));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).update(captor.capture());
        assertEquals(PaymentStatus.PAID, captor.getValue().getStatus());
        assertEquals(PORTONE_PAYMENT_ID, captor.getValue().getPortonePaymentId());
    }

    @Test
    @DisplayName("Transaction.Paid — 금액 불일치 시 상태 변경 없이 무시된다")
    void processWebhook_paid_amountMismatch_ignored() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));
        when(portOnePaymentVerifyPort.getPaymentInfo(PORTONE_PAYMENT_ID))
                .thenReturn(new PortOnePaymentVerifyPort.PortOnePaymentInfo("PAID", 100));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Paid", PORTONE_PAYMENT_ID, ORDER_ID));

        verify(paymentRepository, never()).update(any());
    }

    @Test
    @DisplayName("Transaction.Paid — 이미 PAID 상태면 중복 처리하지 않는다")
    void processWebhook_paid_alreadyPaid_skipped() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(paidPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Paid", PORTONE_PAYMENT_ID, ORDER_ID));

        verify(paymentRepository, never()).update(any());
        verify(portOnePaymentVerifyPort, never()).getPaymentInfo(any());
    }

    // ──── Transaction.Failed ────

    @Test
    @DisplayName("Transaction.Failed — PENDING 상태면 FAILED로 전환된다")
    void processWebhook_failed_success() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Failed", PORTONE_PAYMENT_ID, ORDER_ID));

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
                new ProcessWebhookCommand("Transaction.Failed", PORTONE_PAYMENT_ID, ORDER_ID));

        verify(paymentRepository, never()).update(any());
    }

    // ──── Transaction.Cancelled ────

    @Test
    @DisplayName("Transaction.Cancelled — PAID 상태면 CANCELLED로 전환된다")
    void processWebhook_cancelled_success() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(paidPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Cancelled", PORTONE_PAYMENT_ID, ORDER_ID));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).update(captor.capture());
        assertEquals(PaymentStatus.CANCELLED, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Transaction.Cancelled — 이미 CANCELLED 상태면 중복 처리하지 않는다")
    void processWebhook_cancelled_alreadyCancelled_skipped() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(cancelledPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Cancelled", PORTONE_PAYMENT_ID, ORDER_ID));

        verify(paymentRepository, never()).update(any());
    }

    @Test
    @DisplayName("Transaction.Cancelled — PENDING 상태면 중복 처리하지 않는다")
    void processWebhook_cancelled_pendingStatus_skipped() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(pendingPayment()));

        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Cancelled", PORTONE_PAYMENT_ID, ORDER_ID));

        verify(paymentRepository, never()).update(any());
    }

    // ──── 예외 / unknown ────

    @Test
    @DisplayName("존재하지 않는 orderId면 PaymentNotFoundException이 발생한다")
    void processWebhook_orderNotFound_throwsException() {
        when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> paymentCommandService.processWebhook(
                        new ProcessWebhookCommand("Transaction.Paid", PORTONE_PAYMENT_ID, ORDER_ID)));

        verify(paymentRepository, never()).update(any());
    }

    @Test
    @DisplayName("알 수 없는 웹훅 타입은 무시된다")
    void processWebhook_unknownType_ignored() {
        paymentCommandService.processWebhook(
                new ProcessWebhookCommand("Transaction.Unknown", PORTONE_PAYMENT_ID, ORDER_ID));

        verify(paymentRepository, never()).findByOrderId(any());
        verify(paymentRepository, never()).update(any());
    }
}
