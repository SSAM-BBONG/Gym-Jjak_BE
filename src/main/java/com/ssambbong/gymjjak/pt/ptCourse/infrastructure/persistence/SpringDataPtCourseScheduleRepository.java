package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataPtCourseScheduleRepository extends JpaRepository<PtCourseScheduleJpaEntity, Long> {
    List<PtCourseScheduleJpaEntity> findAllByPtCourseId(Long ptCourseId);

    // 스케줄 ID + 코스 ID로 단건 조회 (소유권 검증용)
    Optional<PtCourseScheduleJpaEntity> findByIdAndPtCourseId(Long id, Long ptCourseId);

    // upsert 시 요청에 없는 스케줄 일괄 삭제
    void deleteAllByIdIn(List<Long> ids);
}
