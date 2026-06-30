package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

import java.util.List;

public record SearchTrainerListResult(
        List<SearchTrainerResult> content,
        int page,
        int size,
        boolean hasNext
) {
}
