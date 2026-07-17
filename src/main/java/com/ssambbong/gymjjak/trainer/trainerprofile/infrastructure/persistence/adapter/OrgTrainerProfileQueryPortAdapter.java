package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.adapter;

import com.ssambbong.gymjjak.organization.organizationTrainer.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrgTrainerProfileQueryPortAdapter implements TrainerProfileQueryPort {

    private final SpringDataTrainerProfileRepository trainerProfileRepository;

    @Override
    public long findActiveTrainerProfileIdByUserId(Long userId) {
        return trainerProfileRepository
                .findTrainerProfileIdByUserIdAndStatus(userId, TrainerProfileStatus.ACTIVE)
                .orElseThrow(() -> new TrainerProfileNotFoundException("userId", userId));
    }
}
