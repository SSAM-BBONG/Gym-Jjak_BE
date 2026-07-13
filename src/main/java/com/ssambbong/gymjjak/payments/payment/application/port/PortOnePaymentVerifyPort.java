package com.ssambbong.gymjjak.payments.payment.application.port;

public interface PortOnePaymentVerifyPort {

    // PortOne API로 결제 건의 실제 상태와 금액을 조회 (웹훅 위변조 방지)
    PortOnePaymentInfo getPaymentInfo(String portonePaymentId);

    record PortOnePaymentInfo(String status, int amount) {}
}
