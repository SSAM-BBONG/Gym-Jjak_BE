package com.ssambbong.gymjjak.payments.payment.application.command;

// 포트원 웹훅 수신
public record ProcessWebhookCommand(
        String type,
        String portonePaymentId,
        String orderId) {}
