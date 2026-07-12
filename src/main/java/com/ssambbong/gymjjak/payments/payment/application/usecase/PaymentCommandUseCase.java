package com.ssambbong.gymjjak.payments.payment.application.usecase;

import com.ssambbong.gymjjak.payments.payment.application.command.CreatePtPaymentCommand;

public interface PaymentCommandUseCase {

    PaymentInitResult createPtPayment(CreatePtPaymentCommand command);

    record PaymentInitResult(String orderId, int amount) {}
}
