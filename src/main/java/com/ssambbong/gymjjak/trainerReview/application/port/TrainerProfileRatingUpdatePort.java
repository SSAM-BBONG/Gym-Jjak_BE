package com.ssambbong.gymjjak.trainerReview.application.port;

public interface TrainerProfileRatingUpdatePort {

    void updateRatingStats(Long trainerProfileId, double averageRating, long reviewCount);
}
