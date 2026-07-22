package com.ssambbong.gymjjak.trainerReport.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BusinessException;

public class TrainerReportAiException extends BusinessException {
    public TrainerReportAiException(TrainerReportErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public TrainerReportAiException(TrainerReportErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
