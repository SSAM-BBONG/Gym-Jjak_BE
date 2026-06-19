package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataFeedbackRepository extends JpaRepository<FeedbackJpaEntity, Long> {

    // 예약 ID로 삭제되지 않은 피드백 전체 조회
    List<FeedbackJpaEntity> findAllByPtReservationIdAndDeletedAtIsNull(Long ptReservationId);

    Optional<FeedbackJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByPtReservationIdAndPtCurriculumIdAndDeletedAtIsNull(Long ptReservationId, Long ptCurriculumId);
}
