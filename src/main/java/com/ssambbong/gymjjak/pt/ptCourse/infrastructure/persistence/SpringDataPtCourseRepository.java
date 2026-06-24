package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {

    // 커리큘럼 수정 시 동시 예약 삽입 방지 — FOR UPDATE 잠금
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PtCourseJpaEntity p WHERE p.id = :id")
    Optional<PtCourseJpaEntity> findByIdForUpdate(@Param("id") Long id);

    // VISIBLE 상태 전체 목록 최신순
    List<PtCourseJpaEntity> findAllByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(PtCourseStatus status);

    // 내 강습 전체 조회 — VISIBLE + HIDDEN만 (BLOCKED, DELETED 제외, soft delete 안전)
    List<PtCourseJpaEntity> findAllByTrainerProfileIdAndStatusInAndDeletedAtIsNullOrderByCreatedAtDesc(
            Long trainerProfileId, List<PtCourseStatus> statuses);

    // 내 강습 특정 status 필터 조회 (soft delete 안전)
    List<PtCourseJpaEntity> findAllByTrainerProfileIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
            Long trainerProfileId, PtCourseStatus status);

    // pt_reservations 수로 GROUP BY 정렬, VISIBLE + soft delete 제외
    @Query(value = """
    SELECT pc.* FROM pt_courses pc
    LEFT JOIN pt_reservations pr ON pr.pt_course_id = pc.pt_course_id
    WHERE pc.deleted_at IS NULL AND pc.status = 'VISIBLE'
    GROUP BY pc.pt_course_id
    ORDER BY COUNT(pr.pt_reservation_id) DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<PtCourseJpaEntity> findPopular(@Param("limit") int limit);
}
