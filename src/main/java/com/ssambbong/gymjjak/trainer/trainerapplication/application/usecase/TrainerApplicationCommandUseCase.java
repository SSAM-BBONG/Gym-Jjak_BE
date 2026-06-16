package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.UpdateTrainerApplicationCommand;

public interface TrainerApplicationCommandUseCase {

    Long createTrainerApplication(CreateTrainerApplicationCommand command);

    Long updateTrainerApplication(UpdateTrainerApplicationCommand command);
}
