package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository.TrainerApplicationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerApplicationRepositoryAdapter implements TrainerApplicationRepository {

    private final SpringDataTrainerApplicationRepository springDataTrainerApplicationRepository;
    private final TrainerApplicationPersistenceMapper trainerApplicationPersistenceMapper;

    // DB에 저장
    @Override
    public TrainerApplication save(TrainerApplication trainerApplication) {
        TrainerApplicationJpaEntity entity =
                trainerApplicationPersistenceMapper.toEntity(trainerApplication);

        TrainerApplicationJpaEntity savedEntity =
                springDataTrainerApplicationRepository.save(entity);

        return trainerApplicationPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TrainerApplication> findById(Long trainerApplicationId) {
        return springDataTrainerApplicationRepository.findById(trainerApplicationId)
                .map(trainerApplicationPersistenceMapper::toDomain);
    }

    // 중복 신청 검증
    @Override
    public boolean getDuplicateBlockingStatuses(Long userId) {
        return springDataTrainerApplicationRepository.existsByUserIdAndStatusIn(
                userId,
                TrainerApplicationStatus.getDuplicateBlockingStatuses()
        );
    }
}
