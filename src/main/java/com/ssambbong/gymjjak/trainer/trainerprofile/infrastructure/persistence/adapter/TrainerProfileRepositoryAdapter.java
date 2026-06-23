package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerProfileJpaEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.mapper.TrainerProfilePersistenceMapper;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class TrainerProfileRepositoryAdapter implements TrainerProfileRepository {

    private final SpringDataTrainerProfileRepository repository;
    private final TrainerProfilePersistenceMapper mapper;

    @Override
    public TrainerProfile save(TrainerProfile trainerProfile) {

        TrainerProfileJpaEntity savedEntity =
                repository.save(mapper.toEntity(trainerProfile));

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TrainerProfile> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<TrainerProfile> findById(Long trainerProfileId) {
        return repository.findById(trainerProfileId)
                .map(mapper::toDomain);
    }
}
