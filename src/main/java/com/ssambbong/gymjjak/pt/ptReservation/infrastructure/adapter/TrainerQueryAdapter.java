package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.TrainerQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("ptReservationTrainerQueryAdapter")
@RequiredArgsConstructor
public class TrainerQueryAdapter implements TrainerQueryPort {

    private final TrainerProfileQueryPort trainerProfileQueryPort;
    private final EntityManager em;

    @Override
    public Optional<Long> findTrainerProfileIdByUserId(Long userId) {
        try {
            return Optional.of(
                    trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(userId)
            );
        } catch (TrainerProfileNotFoundException e) {
            return Optional.empty();
        }
    }

    // TODO: 트레이너 담당자에게 TrainerProfileQueryPortAdapter 구현 요청 후 교체
    @Override
    public Long findUserIdByTrainerProfileId(Long trainerProfileId) {
        return ((Number) em.createNativeQuery(
                        "SELECT user_id FROM trainer_profiles WHERE trainer_profile_id = ?")
                .setParameter(1, trainerProfileId)
                .getSingleResult()).longValue();
    }
}
