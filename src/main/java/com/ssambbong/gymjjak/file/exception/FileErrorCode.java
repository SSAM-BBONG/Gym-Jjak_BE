package com.ssambbong.gymjjak.file.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {

    FILE_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "FILE_000", "잘못된 파일 요청입니다."),
    FILE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "FILE_001", "허용되지 않는 파일 형식입니다."),
    FILE_INVALID_SIZE(HttpStatus.BAD_REQUEST, "FILE_002", "파일 크기가 허용 범위를 초과했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_003", "파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_004", "파일 업로드에 실패했습니다."),
    FILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FILE_005", "파일에 대한 접근 권한이 없습니다."),
    FILE_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "FILE_006", "요청한 파일 타입과 실제 파일 타입이 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
