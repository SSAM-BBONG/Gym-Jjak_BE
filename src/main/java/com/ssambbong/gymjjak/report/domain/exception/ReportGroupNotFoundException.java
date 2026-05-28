package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class ReportGroupNotFoundException extends NotFoundException {

    public ReportGroupNotFoundException(Long reportGroupId) {
        super(ReportErrorCode.REPORT_GROUP_NOT_FOUND);
        addContext("reportGroupId", reportGroupId);
    }
}
