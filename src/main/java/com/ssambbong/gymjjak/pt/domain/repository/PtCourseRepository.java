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
}
