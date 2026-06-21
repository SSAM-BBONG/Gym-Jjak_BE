package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerCertificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataTrainerCertificationRepository extends JpaRepository<TrainerCertificationJpaEntity, Long> {

    List<TrainerCertificationJpaEntity> findAllByTrainerProfileIdOrderByTrainerCertificationIdAsc(
            Long trainerProfileId);
}
