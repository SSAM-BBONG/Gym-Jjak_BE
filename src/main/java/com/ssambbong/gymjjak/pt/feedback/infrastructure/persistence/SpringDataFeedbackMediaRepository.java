package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataFeedbackMediaRepository extends JpaRepository<FeedbackMediaJpaEntity, Long> {

    List<FeedbackMediaJpaEntity> findAllByFeedbackIdOrderByIdAsc(Long feedbackId);
    void deleteAllByFeedbackId(Long feedbackId);
}
