package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataFeedbackMediaRepository extends JpaRepository<FeedbackMediaJpaEntity, Long> {

    List<FeedbackMediaJpaEntity> findAllByFeedbackIdOrderByIdAsc(Long feedbackId);
    void deleteAllByFeedbackId(Long feedbackId);

    // 피드백 ID 목록에 속한 미디어 하드딜리트 (부모 삭제 전 자식 먼저 제거)
    @Modifying
    @Query(value = "DELETE FROM feedback_media WHERE feedback_id IN (:feedbackIds)", nativeQuery = true)
    int hardDeleteByFeedbackIds(@Param("feedbackIds") List<Long> feedbackIds);
}
