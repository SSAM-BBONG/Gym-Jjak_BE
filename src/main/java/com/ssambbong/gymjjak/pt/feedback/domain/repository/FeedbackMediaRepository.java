package com.ssambbong.gymjjak.pt.feedback.domain.repository;

import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;

import java.util.List;

public interface FeedbackMediaRepository {

    List<FeedbackMedia> findAllByFeedbackId(Long feedbackId);

    // 피드백 미디어 일괄 등록
    void saveAll(List<FeedbackMedia> mediaList);
}
