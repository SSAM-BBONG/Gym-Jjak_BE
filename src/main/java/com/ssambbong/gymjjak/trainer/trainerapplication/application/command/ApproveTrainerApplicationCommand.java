package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

public record ApproveTrainerApplicationCommand(
        Long trainerApplicationId,
        Long adminId
) {
}
