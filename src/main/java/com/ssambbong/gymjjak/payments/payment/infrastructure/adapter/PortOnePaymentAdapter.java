package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.PortOnePaymentVerifyPort;
import com.ssambbong.gymjjak.payments.payment.infrastructure.adapter.PortOneApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortOnePaymentAdapter implements PortOnePaymentVerifyPort {

    @Qualifier("portOneRestClient")
    private final RestClient portOneRestClient;

    // PortOne V2 API로 결제 건 조회 — GET /payments/{paymentId}
    @Override
    @SuppressWarnings("unchecked")
    public PortOnePaymentInfo getPaymentInfo(String portonePaymentId) {
        Map<String, Object> response = portOneRestClient.get()
                .uri("/payments/{paymentId}", portonePaymentId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                    log.error("event=portone_api_error portonePaymentId={} status={}", portonePaymentId, res.getStatusCode());
                    throw new PortOneApiException("PortOne API 호출 실패: " + res.getStatusCode());
                })
                .body(Map.class);

        String status = (String) response.get("status");
        Map<String, Object> amount = (Map<String, Object>) response.get("amount");
        int paid = ((Number) amount.get("paid")).intValue();

        log.debug("event=portone_payment_verify portonePaymentId={} status={} amount={}",
                portonePaymentId, status, paid);

        return new PortOnePaymentInfo(status, paid);
    }
}
