package com.ssambbong.gymjjak.pt.trainerReview.application.port;

public interface TrainerProfileRatingUpdatePort {

    void updateRatingStats(Long trainerProfileId, double averageRating, long reviewCount);
}
