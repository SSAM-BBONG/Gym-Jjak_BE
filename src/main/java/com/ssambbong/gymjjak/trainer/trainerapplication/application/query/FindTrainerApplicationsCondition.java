package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;

// 검색 조건 객체
public record FindTrainerApplicationsCondition(
        TrainerApplicationStatus status,
        String keyword,
        int page,
        int size
) {
}
