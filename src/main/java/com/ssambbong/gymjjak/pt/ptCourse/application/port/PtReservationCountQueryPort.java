package com.ssambbong.gymjjak.pt.ptCourse.application.port;

public interface PtReservationCountQueryPort {

    // RESERVED + IN_PROGRESS 상태 예약 수 (활성 수강생 수)
    int countActiveByPtCourseId(Long ptCourseId);

    // 전체 예약 수 (상태 무관)
    int countTotalByPtCourseId(Long ptCourseId);
}
