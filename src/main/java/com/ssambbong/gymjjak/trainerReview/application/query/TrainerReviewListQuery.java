package com.ssambbong.gymjjak.trainerReview.application.query;

public record TrainerReviewListQuery(
        Long trainerProfileId,
        Long cursor,
        Integer cursorRating,
        int size,
        TrainerReviewSortType sort
) {}
