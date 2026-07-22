package com.ssambbong.gymjjak.pt.trainerReview.application.port;

public interface TrainerReviewMetricsPort {

    void recordCreated(int rating);

    void recordUpdated();

    void recordDeleted();
}
