package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SpringDataTrainerProfileRepository extends JpaRepository<TrainerProfileJpaEntity, Long> {

    Optional<TrainerProfileJpaEntity> findByUserId(Long userId);
}
