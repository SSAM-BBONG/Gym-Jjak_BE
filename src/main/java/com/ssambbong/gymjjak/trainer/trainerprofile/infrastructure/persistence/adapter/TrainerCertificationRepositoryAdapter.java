package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertification;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertificationType;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerCertificationRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerCertificationJpaEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.mapper.TrainerCertificationPersistenceMapper;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainerCertificationRepositoryAdapter implements TrainerCertificationRepository {

    private final SpringDataTrainerCertificationRepository repository;
    private final TrainerCertificationPersistenceMapper mapper;

    @Override
    public void saveAll(List<TrainerCertification> trainerCertifications) {
        // 도메인을 엔티티 객체로
        List<TrainerCertificationJpaEntity> entities = trainerCertifications.stream()
                .map(mapper::toEntity)
                .toList();

        repository.saveAll(entities);
    }

    @Override
    public List<TrainerCertification> findAllByTrainerProfileId(Long trainerProfileId) {
        return repository.findAllByTrainerProfileIdOrderByTrainerCertificationIdAsc(
                trainerProfileId
        )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllAdditionalByTrainerProfileId(Long trainerProfileId) {

        repository.deleteAllByTrainerProfileIdAndCertificationType(
                trainerProfileId,
                TrainerCertificationType.ADDITIONAL
        );
    }
}
