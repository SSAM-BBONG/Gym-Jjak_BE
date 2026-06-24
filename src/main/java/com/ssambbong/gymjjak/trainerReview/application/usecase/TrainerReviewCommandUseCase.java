package com.ssambbong.gymjjak.trainerReview.application.usecase;

import com.ssambbong.gymjjak.trainerReview.application.command.CreateTrainerReviewCommand;

public interface TrainerReviewCommandUseCase {

    Long createReview(CreateTrainerReviewCommand command);
}
