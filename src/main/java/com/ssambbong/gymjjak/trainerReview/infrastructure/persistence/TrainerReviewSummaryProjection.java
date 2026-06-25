package com.ssambbong.gymjjak.trainerReview.infrastructure.persistence;

public interface TrainerReviewSummaryProjection {
    String getTrainerName();
    String getIntroduction();
    double getAverageRating();
    long getReviewCount();
    long getRating5Count();
    long getRating4Count();
    long getRating3Count();
    long getRating2Count();
    long getRating1Count();
}
