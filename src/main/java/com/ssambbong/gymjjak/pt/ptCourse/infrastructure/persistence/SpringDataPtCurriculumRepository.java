package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpringDataPtCurriculumRepository extends JpaRepository<PtCurriculumJpaEntity, Long> {
    // 회차 번호 순서대로 반환
    List<PtCurriculumJpaEntity> findAllByPtCourseIdOrderBySessionNoAsc(Long ptCourseId);

    // 커리큘럼 ID + 코스 ID로 단건 조회
    Optional<PtCurriculumJpaEntity> findByIdAndPtCourseId(Long id, Long ptCourseId);

    void deleteAllByIdIn(List<Long> ids);

    // PT 강습 ID 목록에 속한 커리큘럼 하드딜리트 (부모 삭제 전 자식 먼저 제거)
    @Modifying
    @Query(value = "DELETE FROM pt_curriculums WHERE pt_course_id IN :ptCourseIds", nativeQuery = true)
    int hardDeleteByPtCourseIds(@Param("ptCourseIds") List<Long> ptCourseIds);
}
