package com.ssambbong.gymjjak.dashboard.admin.application.usecase;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminContentStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminMemberStatisticsResult;

public interface AdminDashboardQueryUseCase {
    // 회원 통계 현황
    AdminMemberStatisticsResult findMemberStatistics();
    // 콘텐츠 현황
    AdminContentStatisticsResult findContentStatistics();
}
