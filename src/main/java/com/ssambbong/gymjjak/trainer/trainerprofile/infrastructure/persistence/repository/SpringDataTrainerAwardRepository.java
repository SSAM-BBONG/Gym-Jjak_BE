package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerAwardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataTrainerAwardRepository extends JpaRepository<TrainerAwardJpaEntity, Long> {

    List<TrainerAwardJpaEntity> findAllByTrainerProfileIdOrderByTrainerAwardIdAsc(
            Long trainerProfileId);

    @Modifying(flushAutomatically = true)
    @Query("""
        DELETE FROM TrainerAwardJpaEntity award
        WHERE award.trainerProfileId = :trainerProfileId
        """)
    int deleteAllByTrainerProfileId(
            @Param("trainerProfileId") Long trainerProfileId);
}