package com.ssambbong.gymjjak.trainerReview.presentation.api.response;

import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewItem;

import java.time.LocalDateTime;

public record TrainerReviewItemResponse(
        Long trainerReviewId,
        String nickname,
        String ptCourseTitle,
        int rating,
        String content,
        LocalDateTime createdAt
) {
    public static TrainerReviewItemResponse from(TrainerReviewItem item) {
        return new TrainerReviewItemResponse(
                item.trainerReviewId(),
                item.nickname(),
                item.ptCourseTitle(),
                item.rating(),
                item.content(),
                item.createdAt()
        );
    }
}
