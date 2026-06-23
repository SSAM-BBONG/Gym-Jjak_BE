package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

public record SearchTrainerResult(
        Long trainerProfileId,
        String name,
        String username,
        String nickname
) {
}
