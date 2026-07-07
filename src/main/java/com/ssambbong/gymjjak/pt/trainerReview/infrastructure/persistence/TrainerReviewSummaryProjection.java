package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.persistence;

public interface TrainerReviewSummaryProjection {
    String getTrainerName();
    String getIntroduction();
    double getAverageRating();
    Long getReviewCount();
    Long getRating5Count();
    Long getRating4Count();
    Long getRating3Count();
    Long getRating2Count();
    Long getRating1Count();
}
