package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class ReportNotFoundException extends NotFoundException {

    public ReportNotFoundException(Long reportId) {
        super(ReportErrorCode.REPORT_NOT_FOUND);
        addContext("reportId", reportId);
    }

}
