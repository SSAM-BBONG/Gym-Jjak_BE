package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.TrainerProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainerQueryAdapter implements TrainerQueryPort {

    private final TrainerProfileQueryPort trainerProfileQueryPort;

    @Override
    public Optional<Long> findTrainerProfileIdByUserId(Long userId) {
        try {
            return Optional.of(
                    trainerProfileQueryPort.findByUserId(userId).trainerProfileId()
            );
        } catch (TrainerProfileNotFoundException e) {
            return Optional.empty();
        }
    }
}
