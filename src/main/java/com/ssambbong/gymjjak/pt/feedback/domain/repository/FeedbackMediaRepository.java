package com.ssambbong.gymjjak.pt.feedback.domain.repository;

import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;

import java.util.List;

public interface FeedbackMediaRepository {

    List<FeedbackMedia> findAllByFeedbackId(Long feedbackId);

    // 피드백 미디어 일괄 등록
    void saveAll(List<FeedbackMedia> mediaList);

    // 피드백 수정 시 기존 미디어 전체 삭제 (교체)
    void deleteAllByFeedbackId(Long feedbackId);

    // 피드백 ID 목록에 속한 미디어 하드딜리트 (부모 삭제 전 자식 먼저 제거)
    int hardDeleteByFeedbackIds(List<Long> feedbackIds);
}
