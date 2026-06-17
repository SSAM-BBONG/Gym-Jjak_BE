package com.ssambbong.gymjjak.pt.feedback.application.usecase;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackQueryUseCase {

    // 예약 ID로 피드백 목록 조회
    List<FeedbackListView> findFeedbacksByReservation(Long userId, Long ptReservationId);

    // 커리큘럼 1개 + 해당하는 피드백
    record FeedbackListView(
            Long ptCurriculumId,
            int sessionNo,
            String title,
            FeedbackSummary feedback
    ) {}

    // 목록에서 보여줄 피드백 요약 정보
    record FeedbackSummary(
            Long feedbackId,
            String content,
            LocalDate createdAt
    ) {}
}
