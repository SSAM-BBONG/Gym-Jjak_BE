package com.ssambbong.gymjjak.chat.infrastructure.internal;

import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainerQueryAdapter implements TrainerQueryPort {

    private final TrainerProfileRepository trainerProfileRepository;

    @Override
    public Optional<Long> findActiveTrainerUserId(Long trainerProfileId) {
        return trainerProfileRepository.findById(trainerProfileId)
                .filter(profile -> profile.getStatus() == TrainerProfileStatus.ACTIVE)
                .map(TrainerProfile::getUserId);
    }
}
