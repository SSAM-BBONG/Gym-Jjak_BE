package com.ssambbong.gymjjak.dashboard.admin.application.usecase;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminMemberStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminPendingStatisticsResult;

public interface AdminDashboardQueryUseCase {
    // 회원 통계 현황
    AdminMemberStatisticsResult findMemberStatistics();
    // 승인 대기 통계
    AdminPendingStatisticsResult findPendingStatistics();
}
