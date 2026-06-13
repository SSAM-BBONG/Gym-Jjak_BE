package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository.TrainerApplicationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    // 중복 신청 검증
    @Override
    public boolean existsPendingOrApprovedByUserId(Long userId) {
        return springDataTrainerApplicationRepository.existsByUserIdAndStatusIn(
                userId,
                List.of(
                        TrainerApplicationStatus.PENDING,
                        TrainerApplicationStatus.APPROVED
                )
        );
    }
}
