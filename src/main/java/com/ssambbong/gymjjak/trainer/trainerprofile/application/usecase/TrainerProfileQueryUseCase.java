package com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.MyTrainerProfileResult;

public interface TrainerProfileQueryUseCase {

    MyTrainerProfileResult getMyTrainerProfile(Long requesterId);
}
