package com.ssambbong.gymjjak.chat.application.port;

import java.util.Optional;

public interface TrainerQueryPort {
    Optional<TrainerView> findActiveTrainer(Long trainerId);
}
