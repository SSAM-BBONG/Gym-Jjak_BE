package com.ssambbong.gymjjak.pt.ptReservation.application.port;

import java.util.List;

public interface PtCourseQueryPort {

    // pt_courses에서 title, thumbnailFileId 조회
    PtCourseInfo findPtCourseInfo(Long ptCourseId);

    // 해당 pt 강습의 커리큘럼(회차) 목록 조회
    List<CurriculumInfo> findCurriculumsByPtCourseId(Long ptCourseId);

    record PtCourseInfo(String title, Long thumbnailFileId) {}

    record CurriculumInfo(Long id, int sessionNo, String title) {}
}
