package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataPtCurriculumRepository extends JpaRepository<PtCurriculumJpaEntity, Long> {
    // 회차 번호 순서대로 반환
    List<PtCurriculumJpaEntity> findAllByPtCourseIdOrderBySessionNoAsc(Long ptCourseId);

    // 커리큘럼 ID + 코스 ID로 단건 조회
    Optional<PtCurriculumJpaEntity> findByIdAndPtCourseId(Long id, Long ptCourseId);
}
