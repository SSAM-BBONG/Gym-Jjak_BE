package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SpringDataPtCourseScheduleRepository extends JpaRepository<PtCourseScheduleJpaEntity, Long> {
    List<PtCourseScheduleJpaEntity> findAllByPtCourseId(Long ptCourseId);

    void deleteAllByIdIn(List<Long> ids);
}
