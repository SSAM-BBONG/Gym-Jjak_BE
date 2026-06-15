package com.ssambbong.gymjjak.pt.ptCourse.domain.repository;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;

import java.util.List;

public interface PtCourseScheduleRepository {

    List<PtCourseSchedule> saveAll(List<PtCourseSchedule> schedules);
    List<PtCourseSchedule> findAllByPtCourseId(Long ptCourseId);
}
