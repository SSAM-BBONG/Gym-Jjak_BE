package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class ReportGroupNotFoundException extends NotFoundException {

    public ReportGroupNotFoundException(Long reportGroupId) {
        super(ReportErrorCode.REPORT_GROUP_NOT_FOUND);
        addContext("reportGroupId", reportGroupId);
    }

    // String format으로 동적 메시지 저장
//    public class ReportGroupNotFound extends NotFoundException {
//        public ReportGroupNotFoundException(Long reportGroupId) {
//            super(ReportErrorCode.REPORT_GROUP_NOT_FOUND,
//                String.format("신고그룹 ID [%d]를 찾을 수 없습니다.", reportGroupId));
//            addContext("reportGroupId", reportGroupId);
//        }
//    }
}
