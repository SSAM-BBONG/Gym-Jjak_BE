package com.ssambbong.gymjjak.payments.payment.infrastructure.portone;

import com.ssambbong.gymjjak.payments.payment.application.port.PortOnePaymentVerifyPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortOnePaymentAdapter implements PortOnePaymentVerifyPort {

    @Qualifier("portOneRestClient")
    private final RestClient portOneRestClient;

    // PortOne V2 API로 결제 건 조회 — GET /payments/{paymentId}
    @Override
    public PortOnePaymentInfo getPaymentInfo(String portonePaymentId) {
        PortOnePaymentResponse response = portOneRestClient.get()
                .uri("/payments/{paymentId}", portonePaymentId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                    log.error("event=portone_api_error portonePaymentId={} status={}", portonePaymentId, res.getStatusCode());
                    throw new PortOneApiException("PortOne API 호출 실패: " + res.getStatusCode());
                })
                .body(PortOnePaymentResponse.class);

        log.debug("event=portone_payment_verify portonePaymentId={} status={} amount={}",
                portonePaymentId, response.status(), response.amount().total());

        return new PortOnePaymentInfo(response.status(), response.amount().total());
    }
}
