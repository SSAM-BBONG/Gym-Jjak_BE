package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {

    // VISIBLE 상태 전체 목록 최신순
    List<PtCourseJpaEntity> findAllByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(PtCourseStatus status);

    // 내 강습 전체 조회 — VISIBLE + HIDDEN만 (BLOCKED, DELETED 제외, soft delete 안전)
    List<PtCourseJpaEntity> findAllByTrainerProfileIdAndStatusInAndDeletedAtIsNullOrderByCreatedAtDesc(
            Long trainerProfileId, List<PtCourseStatus> statuses);

    // 내 강습 특정 status 필터 조회 (soft delete 안전)
    List<PtCourseJpaEntity> findAllByTrainerProfileIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
            Long trainerProfileId, PtCourseStatus status);
}
