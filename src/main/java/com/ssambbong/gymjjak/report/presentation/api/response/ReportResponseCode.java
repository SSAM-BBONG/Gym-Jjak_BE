package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportResponseCode implements ResponseCode {

    CREATE_REPORT_SUCCESS("REPORT_200_1", "신고가 성공적으로 접수됐습니다."),

    GET_ADMIN_REPORT_LIST_SUCCESS("REPORT_200_2", "신고 목록 조회에 성공했습니다."),
    GET_ADMIN_REPORT_DETAIL_SUCCESS("REPORT_200_3", "신고 상세 조회에 성공했습니다."),
    GET_ADMIN_REPORT_SUMMARY_SUCCESS("REPORT_200_4", "신고 요약 조회에 성공했습니다."),

    APPROVE_REPORT_SUCCESS("REPORT_200_5", "신고 승인 처리에 성공했습니다."),
    REJECT_REPORT_SUCCESS("REPORT_200_6", "신고 반려 처리에 성공했습니다."),
    BLIND_REPORT_TARGET_SUCCESS("REPORT_200_7", "신고 대상 블라인드 처리에 성공했습니다."),
    RELEASE_REPORT_TARGET_SUCCESS("REPORT_200_8", "신고 대상 블라인드 해제에 성공했습니다."),
    MANUAL_BLIND_REPORT_GROUP_SUCCESS("REPORT_200_9", "신고 그룹 수동 블라인드 처리가 성공했습니다." );

    private final String code;
    private final String message;
}
