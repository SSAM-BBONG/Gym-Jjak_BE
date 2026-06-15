package com.ssambbong.gymjjak.pt.ptCourse.domain.repository;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;

import java.util.List;

public interface PtCurriculumRepository {
    List<PtCurriculum> saveAll(List<PtCurriculum> curriculums);
    List<PtCurriculum> findAllByPtCourseId(Long ptCourseId);
}
