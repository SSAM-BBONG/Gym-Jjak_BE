package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPtCurriculumRepository extends JpaRepository<PtCurriculumJpaEntity, Long> {
    // 회차 번호 순서대로 반환
    List<PtCurriculumJpaEntity> findAllByPtCourseIdOrderBySessionNoAsc(Long ptCourseId);
}
