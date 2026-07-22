package com.ssambbong.gymjjak.trainerReport.application.port;

import java.time.LocalDateTime;
import java.util.List;

public interface MarketAggregationPort {
    // [monthStart, monthEndExclusive) 구간에 등록된(취소 제외) 예약을 부위/가격/회차 조합별로 묶어서 수강생 수를 센다.
    // 구간 버킷팅(가격대 등)은 여기서 하지 않고 원본 조합 그대로 반환 — 버킷팅은 application 계층에서 순수 함수로 처리한다.
    List<CourseEnrollmentStat> findMonthlyEnrollmentStats(LocalDateTime monthStart, LocalDateTime monthEndExclusive);

    record CourseEnrollmentStat(String part, int price, int totalSessionCount, long enrollmentCount) {
    }
}
