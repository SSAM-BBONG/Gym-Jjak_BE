package com.ssambbong.gymjjak.pt.ptReservation.application.port;

import java.util.Optional;

public interface TrainerQueryPort {

    // userId -> trainerProfileId 조회
    Optional<Long> findTrainerProfileIdByUserId(Long userId);
    // TODO: 트레이너 담당자 TrainerProfileQueryPortAdapter 구현 후 교체
    Optional<Long> findUserIdByTrainerProfileId(Long trainerProfileId);
}
