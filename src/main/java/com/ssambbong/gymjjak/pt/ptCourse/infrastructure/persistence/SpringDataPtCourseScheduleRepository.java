package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataPtCourseScheduleRepository extends JpaRepository<PtCourseScheduleJpaEntity, Long> {
    List<PtCourseScheduleJpaEntity> findAllByPtCourseId(Long ptCourseId);

    // 스케줄 ID + 코스 ID로 단건 조회 (소유권 검증용)
    Optional<PtCourseScheduleJpaEntity> findByIdAndPtCourseId(Long id, Long ptCourseId);

    // upsert 시 요청에 없는 스케줄 일괄 삭제
    void deleteAllByIdIn(List<Long> ids);

    // PT 강습 ID 목록에 속한 스케줄 하드딜리트 (부모 삭제 전 자식 먼저 제거)
    @Modifying
    @Query(value = "DELETE FROM pt_course_schedules WHERE pt_course_id IN :ptCourseIds", nativeQuery = true)
    int hardDeleteByPtCourseIds(@Param("ptCourseIds") List<Long> ptCourseIds);
}
