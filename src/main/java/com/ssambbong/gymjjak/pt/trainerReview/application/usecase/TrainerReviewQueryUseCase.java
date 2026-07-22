package com.ssambbong.gymjjak.pt.trainerReview.application.usecase;

import com.ssambbong.gymjjak.pt.trainerReview.application.query.TrainerReviewListQuery;
import com.ssambbong.gymjjak.pt.trainerReview.application.query.TrainerReviewListResult;
import com.ssambbong.gymjjak.pt.trainerReview.application.query.TrainerReviewSummary;

public interface TrainerReviewQueryUseCase {

    TrainerReviewSummary getSummary(Long trainerProfileId);

    TrainerReviewListResult getReviews(TrainerReviewListQuery query);
}
