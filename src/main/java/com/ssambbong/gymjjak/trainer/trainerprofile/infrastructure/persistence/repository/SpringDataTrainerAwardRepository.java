package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerAwardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataTrainerAwardRepository extends JpaRepository<TrainerAwardJpaEntity, Long> {

    List<TrainerAwardJpaEntity> findAllByTrainerProfileIdOrderByTrainerAwardIdAsc(
            Long trainerProfileId);
}