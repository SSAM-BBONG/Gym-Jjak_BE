package com.ssambbong.gymjjak.pt.ptCourse.domain.repository;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;

import java.util.List;
import java.util.Optional;

public interface PtCurriculumRepository {
    List<PtCurriculum> saveAll(List<PtCurriculum> curriculums);
    List<PtCurriculum> findAllByPtCourseId(Long ptCourseId);
    Optional<PtCurriculum> findById(Long ptCurriculumId);
    // 커리큘럼 ID + 코스 ID로 단건 조회
    Optional<PtCurriculum> findByIdAndPtCourseId(Long ptCurriculumId, Long ptCourseId);
}
