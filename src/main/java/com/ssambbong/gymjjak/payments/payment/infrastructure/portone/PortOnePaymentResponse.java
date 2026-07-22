package com.ssambbong.gymjjak.payments.payment.infrastructure.portone;

public record PortOnePaymentResponse(
        String status,
        Amount amount
) {
    public record Amount(int total) {}
}
