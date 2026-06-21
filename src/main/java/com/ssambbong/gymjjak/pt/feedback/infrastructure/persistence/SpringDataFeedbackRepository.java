package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
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

    // 예약 ID 목록별 가장 최근 피드백 생성일 배치 조회 (N+1 방지)
    @Query("""
            SELECT f.ptReservationId, MAX(f.createdAt)
            FROM FeedbackJpaEntity f
            WHERE f.ptReservationId IN :reservationIds
              AND f.deletedAt IS NULL
            GROUP BY f.ptReservationId
            """)
    List<Object[]> findLastCreatedAtGroupByReservationId(@Param("reservationIds") List<Long> reservationIds);
}
