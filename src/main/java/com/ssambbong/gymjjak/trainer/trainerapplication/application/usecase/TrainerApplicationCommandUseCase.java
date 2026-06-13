package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;

public interface TrainerApplicationCommandUseCase {

    Long createTrainerApplication(CreateTrainerApplicationCommand command);
}
