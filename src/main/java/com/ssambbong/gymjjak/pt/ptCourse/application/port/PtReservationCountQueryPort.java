package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;
import java.util.Map;

public interface PtReservationCountQueryPort {

    // 강습 ID 목록별 활성(RESERVED + IN_PROGRESS) 예약 수 배치 조회
    // 예약 없는 강습은 0으로 채워진 Map 반환
    Map<Long, Integer> countActiveByPtCourseIds(List<Long> ptCourseIds);

    // 강습 ID 목록별 전체 예약 수 배치 조회
    // 예약 없는 강습은 0으로 채워진 Map 반환
    Map<Long, Integer> countTotalByPtCourseIds(List<Long> ptCourseIds);
}
