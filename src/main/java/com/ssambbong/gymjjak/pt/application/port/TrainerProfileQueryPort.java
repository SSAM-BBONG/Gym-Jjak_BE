package com.ssambbong.gymjjak.pt.application.port;

// 트레이너 프로필 조회 Port
public interface TrainerProfileQueryPort {

    TrainerInfo findByUserId(Long userId);

    record TrainerInfo(
            Long trainerProfileId,
            Long organizationId
    ) {}
}
