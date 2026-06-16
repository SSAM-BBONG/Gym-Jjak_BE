package com.ssambbong.gymjjak.pt.ptReservation.application.port;

import java.time.LocalDate;
import java.util.Map;

public interface FeedbackQueryPort {

    // 피드백 created_at 날짜 (등록 전까진 null)
    // TODO: feedback 도메인 구현 후 실제 조회로 교체
    LocalDate findLastFeedbackDate(Long ptReservationId);

    // 상세 조회용
    Map<Long, Long> findFeedbackIdMapByReservationId(Long ptReservationId);
}
