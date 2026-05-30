package com.ssambbong.gymjjak.pt.domain.repository;

import com.ssambbong.gymjjak.pt.domain.model.PtCourse;

import java.util.List;
import java.util.Optional;

public interface PtCourseRepository {

    // PT 등록
    PtCourse save(PtCourse ptCourse);

    // 단건 조회
    Optional<PtCourse> findById(Long id);

    // 목록 조회
    List<PtCourse> findAllOrderByCreatedAtDesc();

    // 페이지네이션 목록 조회 (VISIBLE 상태만, 필터 선택)
    PtCoursePage findAllVisible(Long categoryId, Long tagId, int page, int size);

    record PtCoursePage(List<PtCourse> content, long totalElements) {}
}
