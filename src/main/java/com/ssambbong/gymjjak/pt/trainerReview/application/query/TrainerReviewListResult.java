package com.ssambbong.gymjjak.pt.trainerReview.application.query;

import java.util.List;

public record TrainerReviewListResult(
        List<TrainerReviewItem> reviews,
        Long nextCursor,
        Integer nextCursorRating,
        boolean hasNext
) {}
