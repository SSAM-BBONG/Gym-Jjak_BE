package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class InvalidReportGroupRelationException extends BadRequestException {

    public InvalidReportGroupRelationException(Long reportGroupId, Long reportId) {
        super(ReportErrorCode.INVALID_REPORT_GROUP_RELATION);
        addContext("reportGroupId", reportGroupId);
        addContext("reportId", reportId);
    }
}
