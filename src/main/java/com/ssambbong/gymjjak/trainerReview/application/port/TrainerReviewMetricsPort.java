package com.ssambbong.gymjjak.trainerReview.application.port;

public interface TrainerReviewMetricsPort {

    void recordCreated(int rating);

    void recordUpdated();

    void recordDeleted();
}
