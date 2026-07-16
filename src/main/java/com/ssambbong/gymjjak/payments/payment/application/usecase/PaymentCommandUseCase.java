package com.ssambbong.gymjjak.payments.payment.application.usecase;

import com.ssambbong.gymjjak.payments.payment.application.command.CreatePtPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.CreateSubscriptionPaymentCommand;
import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;

public interface PaymentCommandUseCase {

    // PT 결제 요청
    PaymentInitResult createPtPayment(CreatePtPaymentCommand command);

    // 구독 결제 요청
    PaymentInitResult createSubscriptionPayment(CreateSubscriptionPaymentCommand command);

    // 웹훅 수신
    void processWebhook(ProcessWebhookCommand command);

    record PaymentInitResult(String orderId, int amount) {}
}
