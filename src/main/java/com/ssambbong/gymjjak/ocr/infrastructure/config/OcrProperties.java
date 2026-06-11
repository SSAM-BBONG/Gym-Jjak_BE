package com.ssambbong.gymjjak.ocr.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ocr")
public record OcrProperties(
        int connectTimeoutMs,   // HTTP 연결 타임아웃 (ms)
        int readTimeoutMs,      // HTTP 읽기 타임아웃 (ms)
        int maxRetryAttempts,   // 최대 재시도 횟수 (초기 시도 제외)
        long retryBackoffMs,    // 재시도 대기 시간 (ms)
        int threadPoolSize,     // OCR 비동기 쓰레드풀 크기
        int threadPoolQueueCapacity // 쓰레드풀 대기 큐 용량
) {
    public OcrProperties {
        if (connectTimeoutMs <= 0) connectTimeoutMs = 3_000;
        if (readTimeoutMs <= 0) readTimeoutMs = 30_000;
        if (maxRetryAttempts < 0) maxRetryAttempts = 2;
        if (retryBackoffMs < 0) retryBackoffMs = 500;
        if (threadPoolSize <= 0) threadPoolSize = 5;
        if (threadPoolQueueCapacity <= 0) threadPoolQueueCapacity = 20;
    }
}
