package com.ssambbong.gymjjak.file.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import lombok.Getter;

/**
 * file 도메인 전용 최상위 예외
 * FileErrorCode를 직접 받아서 code/message를 관리
 * GlobalExceptionHandler가 ApplicationException으로 잡아서 처리
 */
@Getter
public abstract class FileException extends ApplicationException {

    private final FileErrorCode fileErrorCode;

    protected FileException(FileErrorCode fileErrorCode) {
        super(fileErrorCode, fileErrorCode.getMessage());
        this.fileErrorCode = fileErrorCode;
    }

    protected FileException(FileErrorCode fileErrorCode, String message) {
        super(fileErrorCode, message);
        this.fileErrorCode = fileErrorCode;
    }

    protected FileException(FileErrorCode fileErrorCode, Throwable cause) {
        super(fileErrorCode, fileErrorCode.getMessage(), cause);
        this.fileErrorCode = fileErrorCode;
    }
}
