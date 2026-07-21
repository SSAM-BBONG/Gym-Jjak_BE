package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

// 트레이너 pt 메인페이지 조회 시, 카드에 사용되는 db 조회값
public interface TrainerMainPtCourseRow {

    Long getPtCourseId();

    Long getOrganizationId();

    Long getThumbnailFileId();

    String getTitle();

    int getPrice();

    long getCurrentStudentCount();
}
