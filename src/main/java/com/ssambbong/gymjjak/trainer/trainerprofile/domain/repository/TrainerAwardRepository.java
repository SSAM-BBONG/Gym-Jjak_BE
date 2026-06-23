package com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerAward;

import java.util.List;

public interface TrainerAwardRepository {

    void saveAll(List<TrainerAward> trainerAwards);

    List<TrainerAward> findAllByTrainerProfileId(Long trainerProfileId);

    void deleteAllByTrainerProfileId(Long trainerProfileId);
}
