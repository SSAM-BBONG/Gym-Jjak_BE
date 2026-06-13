package com.ssambbong.gymjjak.ocr.infrastructure.clova;

// Spring Retry에서만 사용할 기술 예외
public class ClovaOcrRetryableException extends RuntimeException {

    ClovaOcrRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
