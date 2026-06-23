package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

public record SearchTrainerCondition(
        String keyword,
        int page,
        int size
) {
}
