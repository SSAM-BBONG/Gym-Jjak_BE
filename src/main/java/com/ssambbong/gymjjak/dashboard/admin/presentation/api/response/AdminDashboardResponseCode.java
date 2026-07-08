package com.ssambbong.gymjjak.dashboard.admin.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AdminDashboardResponseCode implements ResponseCode {

    ADMIN_MEMBER_STATISTICS_FOUND(
            "ADMIN_DASHBOARD_200_1",
            "관리자 대시보드 회원 현황 조회에 성공했습니다."
    ),

    ADMIN_PENDING_STATISTICS_FOUND(
        "ADMIN_DASHBOARD_200_2",
                "관리자 대시보드 승인 대기 현황 조회에 성공했습니다."
    ),

    ADMIN_CONTENT_STATISTICS_FOUND(
            "ADMIN_DASHBOARD_200_3",
            "관리자 대시보드 콘텐츠 현황 조회에 성공했습니다."
    );

    private final String code;
    private final String message;
}
