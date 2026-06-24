package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.*;

public interface TrainerApplicationCommandUseCase {

    Long createTrainerApplication(CreateTrainerApplicationCommand command);

    Long updateTrainerApplication(UpdateTrainerApplicationCommand command);

    Long approveTrainerApplication(ApproveTrainerApplicationCommand command);

    void rejectTrainerApplication(RejectTrainerApplicationCommand command);

    void cancelTrainerApplication(CancelTrainerApplicationCommand command);
}
