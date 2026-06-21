package com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertification;
import org.apache.logging.log4j.simple.internal.SimpleProvider;

import java.util.List;

public interface TrainerCertificationRepository {

    void saveAll(List<TrainerCertification> trainerCertifications);

    List<TrainerCertification> findAllByTrainerProfileId(Long trainerProfileId);
}
