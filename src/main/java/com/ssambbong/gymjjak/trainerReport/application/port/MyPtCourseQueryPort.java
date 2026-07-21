package com.ssambbong.gymjjak.trainerReport.application.port;

import java.util.List;

public interface MyPtCourseQueryPort {
    // 노출 중인(VISIBLE, 미삭제) PT 상품만 리포트 비교 대상에 포함한다.
    List<MyPtCourseInfo> findVisibleCoursesByTrainerProfileId(Long trainerProfileId);

    record MyPtCourseInfo(String title, int price, int totalSessionCount, String part) {
    }
}
