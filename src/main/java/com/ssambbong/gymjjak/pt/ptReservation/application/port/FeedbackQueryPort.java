package com.ssambbong.gymjjak.pt.ptReservation.application.port;

import java.util.List;
import java.util.Map;

public interface FeedbackQueryPort {

    // 상세 조회용 — 코스 전체 세션의 커리큘럼ID → 피드백ID 맵
    Map<Long, Long> findFeedbackIdMapByReservationIds(List<Long> ptReservationIds);
}
