package com.ssambbong.gymjjak.pt.application.usecase;

import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;

import java.util.List;

public interface PtCourseQueryUseCase {

    // 목록 조회
    List<PtCourseView> findAllPtCourses();

    // 상세 조회
    PtCourseView findPtCourseDetail(Long ptCourseId);

    record PtCourseView(
            Long ptCourseId,
            Long categoryId,
            Long tagId,
            String thumbnailUrl,  // fileId → URL로 변환해서 반환
            String title,
            String description,
            int price,
            int totalSessionCount,
            PtCourseStatus status
    ) {}
}
