package com.ssambbong.gymjjak.trainerReview.application.query;

import java.util.Map;

public record TrainerReviewSummary(
        String trainerName,
        String introduction,
        double averageRating,
        long reviewCount,
        Map<Integer, Long> ratingDistribution
) {}
