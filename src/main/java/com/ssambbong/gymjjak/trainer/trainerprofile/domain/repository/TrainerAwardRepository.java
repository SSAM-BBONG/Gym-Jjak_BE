package com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerAward;

import java.util.List;
import java.util.Optional;

public interface TrainerAwardRepository {

    void saveAll(List<TrainerAward> trainerAwards);

    List<TrainerAward> findAllByTrainerProfileId(Long trainerProfileId);
}
