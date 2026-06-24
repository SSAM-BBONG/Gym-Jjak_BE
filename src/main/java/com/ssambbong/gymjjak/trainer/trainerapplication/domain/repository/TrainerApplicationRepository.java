package com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;

import java.util.Optional;

public interface TrainerApplicationRepository {

    TrainerApplication save(TrainerApplication trainerApplication);

    Optional<TrainerApplication> findById(Long trainerApplicationId);

    boolean existsDuplicateBlockingApplicationByUserId(Long userId);

    Optional<TrainerApplication> findByIdForUpdate(Long trainerApplicationId);

    void deleteById(Long trainerApplicationId);
}
