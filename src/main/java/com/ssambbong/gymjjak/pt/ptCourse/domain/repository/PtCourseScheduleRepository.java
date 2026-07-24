package com.ssambbong.gymjjak.pt.ptCourse.domain.repository;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;

import java.util.List;

public interface PtCourseScheduleRepository {

    List<PtCourseSchedule> saveAll(List<PtCourseSchedule> schedules);
    List<PtCourseSchedule> findAllByPtCourseId(Long ptCourseId);

    // id가 있는 스케줄 수정 (더티체킹으로 UPDATE)
    void update(PtCourseSchedule schedule);

    // 수정을 신규 삽입보다 먼저 DB에 반영 — UNIQUE(pt_course_id, day_of_week, start_time, end_time) 충돌 방지
    void flush();

    // upsert 시 요청에 없는 스케줄 일괄 삭제
    void deleteAllByIdIn(List<Long> ids);

    // PT 강습 ID 목록에 속한 스케줄 하드딜리트
    int hardDeleteByPtCourseIds(List<Long> ptCourseIds);
}
