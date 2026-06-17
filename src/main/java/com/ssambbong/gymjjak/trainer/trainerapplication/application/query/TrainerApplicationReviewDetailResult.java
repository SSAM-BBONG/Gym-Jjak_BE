package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;

import java.util.List;

public record TrainerApplicationReviewDetailResult(
        Long trainerApplicationId,
        Long userId,
        Long profileImageFileId,
        String name,
        String username,
        String nickname,
        String introduction,
        List<String> qualifications,
        Long certificateFileId,
        List<String> awardHistories,
        TrainerApplicationStatus status
) {
}
