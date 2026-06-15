package com.ssambbong.gymjjak.user.adapter.in.internal;

import com.ssambbong.gymjjak.chat.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.chat.application.port.TrainerView;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainerQueryInternalAdapter implements TrainerQueryPort {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Optional<TrainerView> findActiveTrainer(Long trainerId) {
        return springDataUserRepository
                .findByIdAndRoleAndStatusAndDeletedAtIsNull(trainerId, UserRole.TRAINER, UserStatus.ACTIVE)
                .map(user -> new TrainerView(user.getId()));
    }
}
