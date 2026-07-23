package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.persistence;

import java.time.LocalDateTime;

public interface TrainerReviewProjection {
    Long getTrainerReviewId();
    Long getUserId();
    String getNickname();
    String getPtCourseTitle();
    Integer getRating();
    String getContent();
    LocalDateTime getCreatedAt();
}
