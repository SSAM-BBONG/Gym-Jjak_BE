package com.ssambbong.gymjjak.user.adapter.in.internal;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.TrainerApprovalUserInfo;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAdapterFromTrainerApplication implements TrainerApplicationUserPort {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public TrainerApprovalUserInfo promoteToTrainer(Long userId) {
        log.warn(
                "event=trainer_application_user_stub_promote_to_trainer, userId={}, reason=user_domain_adapter_not_implemented",
                userId
        );
        UserJpaEntity user = springDataUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));


        return new TrainerApprovalUserInfo(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getNickname()
        );
    }
}
