package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.persistence;

import java.time.LocalDateTime;

public interface TrainerReviewProjection {
    Long getTrainerReviewId();
    String getNickname();
    String getPtCourseTitle();
    Integer getRating();
    String getContent();
    LocalDateTime getCreatedAt();
}
