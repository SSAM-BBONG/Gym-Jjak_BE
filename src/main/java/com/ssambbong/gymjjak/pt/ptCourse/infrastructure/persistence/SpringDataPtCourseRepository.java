package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {

    // 결제 포트용 — soft-delete 필터링 포함
    Optional<PtCourseJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    // [dashboard] 조직 소속 PT 목록 + 현재 수강생 수 집계 (트레이너 이름순)
    @Query(value = """
            SELECT pc.pt_course_id        AS ptCourseId,
                   pc.title               AS title,
                   pc.price               AS price,
                   pc.total_session_count AS totalSessionCount,
                   pc.status              AS status,
                   tp.trainer_name        AS trainerName,
                   COUNT(DISTINCT CASE WHEN pr.status = 'IN_PROGRESS' THEN pr.user_id END) AS currentStudentCount
            FROM pt_courses pc
            JOIN trainer_profiles tp ON pc.trainer_profile_id = tp.trainer_profile_id
            LEFT JOIN pt_reservations pr ON pc.pt_course_id = pr.pt_course_id
            WHERE pc.organization_id = :organizationId
            GROUP BY pc.pt_course_id, pc.title, pc.price, pc.total_session_count, pc.status, tp.trainer_name
            ORDER BY tp.trainer_name ASC, pc.pt_course_id DESC
            """, nativeQuery = true)
    List<OrgPtCourseRow> findPtCoursesByOrganizationId(@Param("organizationId") Long organizationId);

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

    // 소프트딜리트된 지 threshold 초과한 PT 강습 ID 배치 조회
    @Query(value = "SELECT pt_course_id FROM pt_courses WHERE deleted_at IS NOT NULL AND deleted_at < :threshold LIMIT :batchSize",
            nativeQuery = true)
    List<Long> findHardDeleteCandidateIds(@Param("threshold") LocalDateTime threshold, @Param("batchSize") int batchSize);

    // 하드딜리트 (deleted_at IS NOT NULL 재검증으로 race condition 방지)
    @Modifying
    @Query(value = "DELETE FROM pt_courses WHERE pt_course_id IN (:ids) AND deleted_at IS NOT NULL", nativeQuery = true)
    int hardDeleteByIds(@Param("ids") List<Long> ids);

    // ── 메트릭용 집계 쿼리 ──

    // 상태별 PT 코스 수
    long countByStatus(PtCourseStatus status);

    // 부위별 PT 코스 수 (소프트딜리트 제외)
    @Query(value = """
            SELECT pc.part, COUNT(pc.pt_course_id)
            FROM pt_courses pc
            WHERE pc.deleted_at IS NULL AND pc.status != 'DELETED'
            GROUP BY pc.part
            ORDER BY COUNT(pc.pt_course_id) DESC
            """, nativeQuery = true)
    List<Object[]> countGroupByPartName();

    // 가격대별 분포 (소프트딜리트 제외)
    @Query(value = """
            SELECT
                CASE
                    WHEN price < 30000 THEN 'under_30k'
                    WHEN price < 50000 THEN '30k_to_50k'
                    WHEN price < 100000 THEN '50k_to_100k'
                    ELSE 'over_100k'
                END AS price_range,
                COUNT(*) AS cnt
            FROM pt_courses
            WHERE deleted_at IS NULL AND status != 'DELETED'
            GROUP BY price_range
            ORDER BY MIN(price)
            """, nativeQuery = true)
    List<Object[]> countGroupByPriceRange();
}
