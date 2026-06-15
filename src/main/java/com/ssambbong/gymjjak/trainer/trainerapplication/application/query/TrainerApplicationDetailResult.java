package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TrainerApplicationDetailResult(
        Long trainerApplicationId,
        Long userId,
        Long profileImageFileId,
        Long certificateFileId,
        List<String> qualifications,
        List<String> awardHistories,
        String introduction,
        TrainerApplicationStatus status,
        String rejectReason,
        Long reviewedBy,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
