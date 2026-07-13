package com.ssambbong.gymjjak.payments.payment.application.service;

import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionPaymentQueryPort;
import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentQueryServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private PtCoursePaymentQueryPort ptCoursePaymentQueryPort;
    @Mock private SubscriptionPaymentQueryPort subscriptionPaymentQueryPort;

    @InjectMocks
    private PaymentQueryService paymentQueryService;

    private static final Long USER_ID = 1L;
    private static final Long PT_COURSE_ID = 10L;

    @Test
    @DisplayName("PAID 상태 결제가 존재하면 isPtCoursePurchased는 true를 반환한다")
    void isPtCoursePurchased_returnsTrueWhenPaid() {
        when(paymentRepository.existsByUserIdAndPtCourseIdAndStatus(USER_ID, PT_COURSE_ID, PaymentStatus.PAID))
                .thenReturn(true);

        assertTrue(paymentQueryService.isPtCoursePurchased(USER_ID, PT_COURSE_ID));
    }

    @Test
    @DisplayName("PAID 상태 결제가 없으면 isPtCoursePurchased는 false를 반환한다")
    void isPtCoursePurchased_returnsFalseWhenNotPaid() {
        when(paymentRepository.existsByUserIdAndPtCourseIdAndStatus(USER_ID, PT_COURSE_ID, PaymentStatus.PAID))
                .thenReturn(false);

        assertFalse(paymentQueryService.isPtCoursePurchased(USER_ID, PT_COURSE_ID));
    }
}
