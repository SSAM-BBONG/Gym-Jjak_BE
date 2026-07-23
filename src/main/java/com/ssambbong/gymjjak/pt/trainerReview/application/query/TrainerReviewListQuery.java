package com.ssambbong.gymjjak.pt.trainerReview.application.query;

public record TrainerReviewListQuery(
        Long trainerProfileId,
        Long cursor,
        Integer cursorRating,
        int size,
        TrainerReviewSortType sort,
        Long requesterId
) {}
