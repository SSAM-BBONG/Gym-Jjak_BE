package com.ssambbong.gymjjak.organization.organizationTrainer.application.port;

public interface TrainerProfileQueryPort {

    // userId로 TrainerProfileId 조회
    long findActiveTrainerProfileIdByUserId(Long userId);
}
