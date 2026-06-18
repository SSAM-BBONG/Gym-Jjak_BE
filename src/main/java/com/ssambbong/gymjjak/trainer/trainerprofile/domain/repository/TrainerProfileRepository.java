package com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;

public interface TrainerProfileRepository {

    TrainerProfile save(TrainerProfile trainerProfile);
}
