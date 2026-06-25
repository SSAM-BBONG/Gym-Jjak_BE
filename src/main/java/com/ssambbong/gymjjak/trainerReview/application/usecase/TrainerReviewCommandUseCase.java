package com.ssambbong.gymjjak.trainerReview.application.usecase;

import com.ssambbong.gymjjak.trainerReview.application.command.CreateTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.command.DeleteTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.command.UpdateTrainerReviewCommand;

public interface TrainerReviewCommandUseCase {

    Long createReview(CreateTrainerReviewCommand command);

    Long updateReview(UpdateTrainerReviewCommand command);

    void deleteReview(DeleteTrainerReviewCommand command);
}
