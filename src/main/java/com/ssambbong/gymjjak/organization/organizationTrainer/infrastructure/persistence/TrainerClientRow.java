package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

// [dashboard] 트레이너별 누적 수강생 수 조회용 projection
public interface TrainerClientRow {
    Long getTrainerProfileId();
    String getTrainerName();
    Double getAverageRating();
    Long getClientCount();
}
