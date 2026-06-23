package com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateTrainerProfileCommand;

public interface TrainerProfileCommandUseCase {

    Long updateMyTrainerProfile(UpdateTrainerProfileCommand command);
}
