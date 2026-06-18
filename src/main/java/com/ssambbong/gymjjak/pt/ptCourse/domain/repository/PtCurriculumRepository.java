package com.ssambbong.gymjjak.pt.ptCourse.domain.repository;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;

import java.util.List;
import java.util.Optional;

public interface PtCurriculumRepository {
    List<PtCurriculum> saveAll(List<PtCurriculum> curriculums);
    List<PtCurriculum> findAllByPtCourseId(Long ptCourseId);
    Optional<PtCurriculum> findById(Long ptCurriculumId);
}
