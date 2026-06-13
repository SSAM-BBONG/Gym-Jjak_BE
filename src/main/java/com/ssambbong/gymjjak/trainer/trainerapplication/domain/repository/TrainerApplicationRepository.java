package com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;

public interface TrainerApplicationRepository {

    TrainerApplication save(TrainerApplication trainerApplication);

    boolean existsPendingOrApprovedByUserId(Long userId);
}
