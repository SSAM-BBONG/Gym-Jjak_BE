package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class ReportGroupSoftDeleteFailedException extends ConflictException {

    public ReportGroupSoftDeleteFailedException(Long reportGroupId) {
        super(ReportErrorCode.REPORT_GROUP_SOFT_DELETE_FAILED);
        addContext("reportGroupId", reportGroupId);
    }
}
