package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;

import java.util.Optional;

public interface TrainerApplicationQueryPort {
    Optional<TrainerApplicationDetailResult> findLatestDetailByUserId(Long userId);
}
