package com.ssambbong.gymjjak.dashboard.admin.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminMemberStatisticsResult;
import lombok.Builder;

import java.util.List;

@Builder
public record AdminMemberStatisticsResponse(
        // 전체 이용자 수
        long totalUserCount,
        // 전체 트레이너 수
        long totalTrainerCount,
        // 전체 헬스장 수
        long totalOrganizationCount,
        // 월별 사용자 수
        List<MonthlyUserSignupResponse> monthlyUserSignups
) {
    public static AdminMemberStatisticsResponse from(
            AdminMemberStatisticsResult result
    ) {
        return AdminMemberStatisticsResponse.builder()
                .totalUserCount(result.totalUserCount())
                .totalTrainerCount(result.totalTrainerCount())
                .totalOrganizationCount(result.totalOrganizationCount())
                .monthlyUserSignups(
                        result.monthlyUserSignups().stream()
                                .map(MonthlyUserSignupResponse::from)
                                .toList()
                )
                .build();
    }
}
