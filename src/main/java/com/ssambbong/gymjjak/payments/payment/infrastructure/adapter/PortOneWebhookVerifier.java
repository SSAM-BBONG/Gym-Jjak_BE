package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.domain.exception.WebhookInvalidSignatureException;
import com.ssambbong.gymjjak.payments.payment.infrastructure.config.PortOneProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

// 웹훅 시그니처 검증
@Slf4j
@Component
@RequiredArgsConstructor
public class PortOneWebhookVerifier {

    private static final String WHSEC_PREFIX = "whsec_";
    // PortOne이 5분 이내에 발송한 웹훅만 유효로 처리 (재전송 공격 방어)
    private static final long TOLERANCE_SECONDS = 300L;

    private final PortOneProperties portOneProperties;

    // 포트원이 보낸 '웹훅 시그니처 헤더'에 담긴 값 (헤더들 + 시크릿 + 본문)을 검증
    public void verify(String rawBody, String msgId, String msgTimestamp, String msgSignature) {

        // 헤더 존재 확인
        if (msgId == null || msgTimestamp == null || msgSignature == null) {
            log.warn("event=webhook_signature_header_missing msgId={} hasTimestamp={} hasSignature={}",
                    msgId, msgTimestamp != null, msgSignature != null);
            throw new WebhookInvalidSignatureException();
        }
        // timestamp 문자열 -> 숫자 변환
        long ts;
        try {
            ts = Long.parseLong(msgTimestamp);
        } catch (NumberFormatException e) {
            log.warn("event=webhook_timestamp_invalid msgTimestamp={}", msgTimestamp);
            throw new WebhookInvalidSignatureException();
        }
        // timestamp 시간 검증 (5분 지났거나 - 재전송 공격 / 현재보다 5분 앞선 미래값 - 위조)
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - ts) > TOLERANCE_SECONDS) {
            log.warn("event=webhook_timestamp_expired ts={} now={} diffSeconds={}", ts, now, Math.abs(now - ts));
            throw new WebhookInvalidSignatureException();
        }

        // 시크릿 디코딩
        byte[] keyBytes = decodeSecret(portOneProperties.getWebhookSecret());

        // 서명 대상 문자열: {webhook-id}.{webhook-timestamp}.{rawBody}
        String signedContent = msgId + "." + msgTimestamp + "." + rawBody;
        String expectedSig = computeSignature(keyBytes, signedContent);

        // 포트원 vs 우리 계산 값 비교
        boolean valid = Arrays.stream(msgSignature.split(" "))
                .anyMatch(sig -> sig.equals(expectedSig));

        if (!valid) {
            log.warn("event=webhook_signature_mismatch msgId={}", msgId);
            throw new WebhookInvalidSignatureException();
        }
    }

    // 포트원이 주는 시크릿(whsec_ + Base64 형태) 디코딩하기
    private byte[] decodeSecret(String secret) {
        try {
            String encoded = secret.startsWith(WHSEC_PREFIX)
                    ? secret.substring(WHSEC_PREFIX.length())
                    : secret;
            return Base64.getDecoder().decode(encoded);
        } catch (IllegalArgumentException e) {
            log.error("event=webhook_secret_decode_failed");
            throw new WebhookInvalidSignatureException();
        }
    }

    // HMAC-SHA256 계산 후 "v1,{Base64}" 형식으로 반환
    private String computeSignature(byte[] keyBytes, String signedContent) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));
            byte[] hmac = mac.doFinal(signedContent.getBytes(StandardCharsets.UTF_8));
            return "v1," + Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 computation failed", e);
        }
    }
}
