package com.ssambbong.gymjjak.chat.application.port;

import java.util.Optional;

public interface TrainerQueryPort {
    Optional<Long> findActiveTrainerUserId(Long trainerProfileId);
}
