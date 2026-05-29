package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class SelfReportNotAllowedException extends BadRequestException {

    public SelfReportNotAllowedException(Long reporterId) {
        super(ReportErrorCode.SELF_REPORT_NOT_ALLOWED);
        addContext("reporterId", reporterId);
    }
}
