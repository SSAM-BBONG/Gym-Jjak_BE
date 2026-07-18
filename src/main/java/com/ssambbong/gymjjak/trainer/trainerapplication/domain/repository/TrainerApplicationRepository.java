package com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;

import java.util.List;
import java.util.Optional;

public interface TrainerApplicationRepository {

    TrainerApplication save(TrainerApplication trainerApplication);

    List<TrainerApplication> saveAll(
            List<TrainerApplication> trainerApplications
    );

    Optional<TrainerApplication> findById(Long trainerApplicationId);

    boolean existsDuplicateBlockingApplicationByUserIdAndOrganizationIds(
            Long userId,
            List<Long> organizationIds
    );

    Optional<TrainerApplication> findByIdForUpdate(Long trainerApplicationId);

    void deleteById(Long trainerApplicationId);
}
