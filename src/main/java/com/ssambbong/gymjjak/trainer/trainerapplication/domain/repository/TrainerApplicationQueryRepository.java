package com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;

import java.util.Optional;

public interface TrainerApplicationQueryRepository {

    Optional<TrainerApplicationDetailResult> findLatestDetailByUserId(Long userId);
}
