package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;
import java.util.Map;

public interface PtReservationCountQueryPort {

    record StudentCounts(Map<Long, Integer> active, Map<Long, Integer> total) {}

    // 강습 ID 목록별 현재 수강 중/전체 수강생 수를 한 번에 배치 조회
    // 예약 없는 강습은 0으로 초기화된 Map 반환
    StudentCounts countStudentsByPtCourseIds(List<Long> ptCourseIds);
}
