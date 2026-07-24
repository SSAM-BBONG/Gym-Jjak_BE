package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataFeedbackRepository extends JpaRepository<FeedbackJpaEntity, Long> {

    // 예약 ID로 삭제되지 않은 피드백 전체 조회
    List<FeedbackJpaEntity> findAllByPtReservationIdAndDeletedAtIsNull(Long ptReservationId);

    // 예약 ID 목록으로 삭제되지 않은 피드백 전체 조회
    List<FeedbackJpaEntity> findAllByPtReservationIdInAndDeletedAtIsNull(List<Long> ptReservationIds);

    // 피드백 단건 조회
    Optional<FeedbackJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    // 코스 전체 세션 중 동일 커리큘럼 피드백 중복 여부 확인
    boolean existsByPtReservationIdInAndPtCurriculumIdAndDeletedAtIsNull(List<Long> ptReservationIds, Long ptCurriculumId);

    // 타입 안전 프로젝션 — Object[] 캐스팅 없이 reservationId·lastCreatedAt 바인딩
    interface LastFeedbackRow {
        Long getPtReservationId();
        LocalDateTime getLastCreatedAt();
    }

    // 예약 ID 목록별 가장 최근 피드백 생성일 배치 조회 (N+1 방지)
    @Query("""
            SELECT f.ptReservationId AS ptReservationId, MAX(f.createdAt) AS lastCreatedAt
            FROM FeedbackJpaEntity f
            WHERE f.ptReservationId IN :reservationIds
              AND f.deletedAt IS NULL
            GROUP BY f.ptReservationId
            """)
    List<LastFeedbackRow> findLastCreatedAtGroupByReservationId(@Param("reservationIds") List<Long> reservationIds);

    // 소프트딜리트된 지 threshold 초과한 피드백 ID 배치 조회
    @Query(value = "SELECT feedback_id FROM feedbacks WHERE deleted_at IS NOT NULL AND deleted_at < :threshold LIMIT :batchSize",
            nativeQuery = true)
    List<Long> findHardDeleteCandidateIds(@Param("threshold") LocalDateTime threshold, @Param("batchSize") int batchSize);

    // 하드딜리트 (deleted_at IS NOT NULL 재검증으로 race condition 방지)
    @Modifying
    @Query(value = "DELETE FROM feedbacks WHERE feedback_id IN (:ids) AND deleted_at IS NOT NULL", nativeQuery = true)
    int hardDeleteByIds(@Param("ids") List<Long> ids);

    // PT 강습 연쇄 삭제 전용 하드딜리트 — deleted_at 무관하게 삭제 (활성 피드백도 코스와 함께 정리)
    @Modifying
    @Query(value = "DELETE FROM feedbacks WHERE feedback_id IN (:ids)", nativeQuery = true)
    int forceHardDeleteByIds(@Param("ids") List<Long> ids);

    // PT 강습 ID 목록에 속한 피드백 ID 조회 (리텐션 삭제 순서 보장용 — feedback_media 먼저 삭제 후 사용)
    @Query(value = """
            SELECT f.feedback_id FROM feedbacks f
            WHERE f.pt_curriculum_id IN (
                SELECT c.pt_curriculum_id FROM pt_curriculums c WHERE c.pt_course_id IN (:ptCourseIds)
            )
            """, nativeQuery = true)
    List<Long> findIdsByPtCourseIds(@Param("ptCourseIds") List<Long> ptCourseIds);

    // ── 메트릭용 집계 쿼리 ──

    // 활성 피드백 전체 수
    long countByDeletedAtIsNull();

    // 피드백이 존재하는 예약 수 (distinct)
    @Query("SELECT COUNT(DISTINCT f.ptReservationId) FROM FeedbackJpaEntity f WHERE f.deletedAt IS NULL")
    long countDistinctReservationsWithFeedback();

    // 트레이너별 피드백 작성 소요 시간 평균 (예약 시작 후 첫 피드백 작성까지, 시간 단위)
    @Query(value = """
            SELECT f.trainer_profile_id,
                   AVG(TIMESTAMPDIFF(HOUR, r.reserved_start_at, f.created_at)) AS avg_hours
            FROM feedbacks f
            JOIN pt_reservations r ON r.pt_reservation_id = f.pt_reservation_id
            WHERE f.deleted_at IS NULL
            GROUP BY f.trainer_profile_id
            ORDER BY avg_hours ASC
            """, nativeQuery = true)
    List<Object[]> findAvgFeedbackHoursByTrainer();
}
