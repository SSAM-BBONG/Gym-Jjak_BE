package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerAwardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTrainerAwardRepository extends JpaRepository<TrainerAwardJpaEntity, Long> {
}