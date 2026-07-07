package com.ssambbong.gymjjak.pt.trainerReview.application.usecase;

import com.ssambbong.gymjjak.pt.trainerReview.application.command.CreateTrainerReviewCommand;
import com.ssambbong.gymjjak.pt.trainerReview.application.command.DeleteTrainerReviewCommand;
import com.ssambbong.gymjjak.pt.trainerReview.application.command.UpdateTrainerReviewCommand;

public interface TrainerReviewCommandUseCase {

    Long createReview(CreateTrainerReviewCommand command);

    Long updateReview(UpdateTrainerReviewCommand command);

    void deleteReview(DeleteTrainerReviewCommand command);
}
