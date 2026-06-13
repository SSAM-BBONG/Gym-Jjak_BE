package com.ssambbong.gymjjak.ocr.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OcrErrorCode implements ErrorCode {

    OCR_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "OCR_502_1", "OCR 요청에 실패했습니다."),
    OCR_INVALID_RESPONSE(HttpStatus.BAD_GATEWAY, "OCR_502_2", "OCR 응답 형식이 올바르지 않습니다."),
    OCR_FILE_READ_FAILED(HttpStatus.BAD_REQUEST, "OCR_400_1", "OCR 파일을 읽을 수 없습니다."),
    OCR_UNSUPPORTED_FILE_FORMAT(HttpStatus.BAD_REQUEST, "OCR_400_2", "지원하지 않는 OCR 파일 형식입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
