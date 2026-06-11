package com.ssambbong.gymjjak.ocr.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.InfrastructureException;
import org.springframework.http.HttpStatus;

public class OcrException extends InfrastructureException {

    public OcrException(OcrErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public OcrException(OcrErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }

    public OcrException(OcrErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
