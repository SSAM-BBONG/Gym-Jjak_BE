package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;

import java.time.LocalDateTime;

// 목록 개별 result
public record TrainerApplicationSummaryResult(
        Long trainerApplicationId,
        Long userId,
        String username,
        String name,
        String nickname,
        TrainerApplicationStatus status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {
}
