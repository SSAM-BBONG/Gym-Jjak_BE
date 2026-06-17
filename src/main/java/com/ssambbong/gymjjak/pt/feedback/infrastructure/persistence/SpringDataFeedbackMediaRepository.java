package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataFeedbackMediaRepository extends JpaRepository<FeedbackMediaJpaEntity, Long> {

    // 피드백 ID로 미디어 목록 조회
    List<FeedbackMediaJpaEntity> findAllByFeedbackId(Long feedbackId);
}
