package com.ssambbong.gymjjak.pt.feedback.domain.repository;

import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository {

    // 예약 ID로 피드백 목록 조회
    List<Feedback> findAllByPtReservationId(Long ptReservationId);

    // 피드백 ID로 단건 조회
    Optional<Feedback> findById(Long feedbackId);

    // 피드백 등록
    Feedback save(Feedback feedback);

    // 동일 예약 - 회차 피드백 중복 여부 확인
    boolean existsByPtReservationIdAndPtCurriculumId(Long ptReservationId, Long ptCurriculumId);
}
