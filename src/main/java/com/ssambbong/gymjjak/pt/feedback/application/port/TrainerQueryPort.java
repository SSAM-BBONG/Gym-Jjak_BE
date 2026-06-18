package com.ssambbong.gymjjak.pt.feedback.application.port;

import java.util.Optional;

public interface TrainerQueryPort {

    // userId
    Optional<Long> findTrainerProfileIdByUserId(Long userId);
}
