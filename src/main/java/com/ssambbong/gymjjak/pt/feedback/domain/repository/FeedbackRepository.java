package com.ssambbong.gymjjak.pt.feedback.domain.repository;

import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository {

    // 예약 ID로 피드백 목록 조회
    List<Feedback> findAllByPtReservationId(Long ptReservationId);

    // 피드백 ID로 단건 조회
    Optional<Feedback> findById(Long feedbackId);
}
