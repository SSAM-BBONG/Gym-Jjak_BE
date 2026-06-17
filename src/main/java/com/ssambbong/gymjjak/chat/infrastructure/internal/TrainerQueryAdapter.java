package com.ssambbong.gymjjak.chat.infrastructure.internal;

import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.chat.application.port.TrainerView;
import org.springframework.stereotype.Component;

import java.util.Optional;

// TODO: user 도메인 팀 구현 완료 후 해당 어댑터로 교체
@Component
public class TrainerQueryAdapter implements TrainerQueryPort {

    @Override
    public Optional<TrainerView> findActiveTrainer(Long trainerProfileId) {
        return Optional.of(new TrainerView(trainerProfileId));
    }
}
