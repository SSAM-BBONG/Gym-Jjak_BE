package com.ssambbong.gymjjak.pt.feedback.domain.repository;

import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;

import java.util.List;

public interface FeedbackMediaRepository {

    List<FeedbackMedia> findAllByFeedbackId(Long feedbackId);
}
