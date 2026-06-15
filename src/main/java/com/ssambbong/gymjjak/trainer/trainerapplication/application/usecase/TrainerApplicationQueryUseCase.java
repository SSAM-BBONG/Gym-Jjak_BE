package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;

public interface TrainerApplicationQueryUseCase {

    TrainerApplicationDetailResult getMyTrainerApplication(Long requesterId);
}
