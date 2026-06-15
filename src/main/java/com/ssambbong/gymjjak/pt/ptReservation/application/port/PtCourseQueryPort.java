package com.ssambbong.gymjjak.pt.ptReservation.application.port;

public interface PtCourseQueryPort {

    // pt_courses에서 title, thumbnailFileId 조회
    PtCourseInfo findPtCourseInfo(Long ptCourseId);

    record PtCourseInfo(String title, Long thumbnailFileId) {}
}
