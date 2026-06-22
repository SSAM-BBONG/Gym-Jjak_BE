package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CourseReservationFeedbackQueryPort {

    // 예약 ID 목록별 가장 최근 피드백 날짜 배치 조회
    // 피드백 없는 예약은 Map에 포함되지 않음 (getOrDefault로 null 처리)
    Map<Long, LocalDate> findLastFeedbackDatesByReservationIds(List<Long> reservationIds);
}
