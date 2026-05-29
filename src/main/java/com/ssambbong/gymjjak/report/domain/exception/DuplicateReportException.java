package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class DuplicateReportException extends ConflictException {

    public DuplicateReportException(Long reporterId, Long reportGroupId) {
        super(ReportErrorCode.DUPLICATE_REPORT);
        addContext("reporterId", reporterId);
        addContext("reportGroupId", reportGroupId);
    }
}
