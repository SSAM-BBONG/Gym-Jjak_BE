package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.DuplicateTrainerApplicationException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository.TrainerApplicationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

        try {
            TrainerApplicationJpaEntity entity =
                    trainerApplicationPersistenceMapper.toEntity(trainerApplication);

            TrainerApplicationJpaEntity savedEntity =
                    springDataTrainerApplicationRepository.save(entity);

            return trainerApplicationPersistenceMapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateBlockingUserConstraint(exception)) {
                // 409 conflict로 예외 처리
                throw new DuplicateTrainerApplicationException(trainerApplication.getUserId());
            }
            throw exception;
        }
    }

    // DB 제약 위반 에러 중, 우리가 만든 유니크 index 때문에 발생한 에러인지 확인하는 메서드
    private boolean isDuplicateBlockingUserConstraint(DataIntegrityViolationException exception) {
        String message = exception.getMostSpecificCause().getMessage();

        return message != null
                // 에러 메시지 중 아래 index 문자열이 있으면 true
                && message.contains("uk_trainer_applications_duplicate_blocking_user");
    }

    @Override
    public Optional<TrainerApplication> findById(Long trainerApplicationId) {
        return springDataTrainerApplicationRepository.findById(trainerApplicationId)
                .map(trainerApplicationPersistenceMapper::toDomain);
    }

    // 중복 신청 검증
    @Override
    public boolean existsDuplicateBlockingApplicationByUserId(Long userId) {
        return springDataTrainerApplicationRepository.existsByUserIdAndStatusIn(
                userId,
                TrainerApplicationStatus.getDuplicateBlockingStatuses()
        );
    }
}
