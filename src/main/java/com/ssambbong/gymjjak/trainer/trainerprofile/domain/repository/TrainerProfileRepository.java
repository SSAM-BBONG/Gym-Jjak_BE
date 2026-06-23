package com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;

import java.util.Optional;

public interface TrainerProfileRepository {

    TrainerProfile save(TrainerProfile trainerProfile);

    Optional<TrainerProfile> findByUserId(Long userId);

    Optional<TrainerProfile> findById(Long trainerProfileId);
}
