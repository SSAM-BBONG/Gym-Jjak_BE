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

    // 피드백 단건 조회
    Optional<FeedbackJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    // 동일 예약 + 커리큘럼에 피드백 중복 여부 확인
    boolean existsByPtReservationIdAndPtCurriculumIdAndDeletedAtIsNull(Long ptReservationId, Long ptCurriculumId);

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
}
