package com.ssambbong.gymjjak.organization.organizationTrainer.application.query;

// [dashboard] 트레이너별 누적 수강생 수 조회 결과
public record TrainerClientSummary(
        Long trainerProfileId,
        String trainerName,
        double averageRating,
        long clientCount
) {}
