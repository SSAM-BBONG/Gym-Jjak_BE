package com.ssambbong.gymjjak.ocr.infrastructure.clova;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "clova.ocr")
public record ClovaOcrProperties(
        String invokeUrl,
        String secretKey
) {

    public ClovaOcrProperties {
        if (!StringUtils.hasText(invokeUrl)) {
            throw new IllegalArgumentException("Clova OCR invokeUrl은 필수입니다.");
        }

        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalArgumentException("Clova OCR secretKey는 필수입니다.");
        }
    }
}
