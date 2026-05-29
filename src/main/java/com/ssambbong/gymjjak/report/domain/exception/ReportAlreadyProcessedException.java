package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class ReportAlreadyProcessedException extends ConflictException {

    public ReportAlreadyProcessedException(Long reportId) {
        super(ReportErrorCode.REPORT_ALREADY_PROCESSED);
        addContext("reportId", reportId);
    }
}
