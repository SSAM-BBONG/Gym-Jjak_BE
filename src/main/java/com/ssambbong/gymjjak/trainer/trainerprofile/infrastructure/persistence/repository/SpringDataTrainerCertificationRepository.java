package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertificationType;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerCertificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataTrainerCertificationRepository extends JpaRepository<TrainerCertificationJpaEntity, Long> {

    List<TrainerCertificationJpaEntity> findAllByTrainerProfileIdOrderByTrainerCertificationIdAsc(
            Long trainerProfileId);


    @Modifying(flushAutomatically = true)
    @Query("""
            delete from TrainerCertificationJpaEntity certification
            where certification.trainerProfileId = :trainerProfileId
                and certification.certificationType = :certificationType
    """)
    int deleteAllByTrainerProfileIdAndCertificationType(
            @Param("trainerProfileId") Long trainerProfileId,
            @Param("certificationType") TrainerCertificationType certificationType
    );
}
