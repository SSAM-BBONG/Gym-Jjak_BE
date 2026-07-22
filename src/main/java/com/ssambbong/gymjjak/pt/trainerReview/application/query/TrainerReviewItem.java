package com.ssambbong.gymjjak.pt.trainerReview.application.query;

import java.time.LocalDateTime;

public record TrainerReviewItem(
        Long trainerReviewId,
        String nickname,
        String ptCourseTitle,
        int rating,
        String content,
        LocalDateTime createdAt
) {}
