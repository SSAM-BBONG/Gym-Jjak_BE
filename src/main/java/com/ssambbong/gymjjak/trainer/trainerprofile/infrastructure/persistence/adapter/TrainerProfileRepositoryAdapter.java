package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerProfileJpaEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.mapper.TrainerProfilePersistenceMapper;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class TrainerProfileRepositoryAdapter implements TrainerProfileRepository, TrainerQueryPort {


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

    // chat 도메인의 TrainerQueryPort 구현 — trainerProfileId로 ACTIVE 트레이너의 userId 조회
    @Override
    public Optional<Long> findActiveTrainerUserId(Long trainerProfileId) {
        return repository.findById(trainerProfileId)
                .map(mapper::toDomain)
                .filter(profile -> profile.getStatus() == TrainerProfileStatus.ACTIVE)
                .map(TrainerProfile::getUserId);
    }
}
