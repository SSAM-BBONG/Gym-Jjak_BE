package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class ReportGroupCountUnderflowException extends ConflictException {

    public ReportGroupCountUnderflowException(Long reportGroupId, int effectiveReportCount) {
        super(ReportErrorCode.REPORT_GROUP_EFFECTIVE_COUNT_UNDERFLOW);
        addContext("reportGroupId", reportGroupId);
        addContext("effectiveReportCount", effectiveReportCount);
    }
}
