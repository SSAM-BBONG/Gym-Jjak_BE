package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;

import java.time.LocalDateTime;

// 트레이너 신청서 목록 조회 요약 정보
public record MyTrainerApplicationSummaryResult(
        Long trainerApplicationId,
        String organizationName,
        TrainerApplicationStatus status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        String rejectReason
) {
}
