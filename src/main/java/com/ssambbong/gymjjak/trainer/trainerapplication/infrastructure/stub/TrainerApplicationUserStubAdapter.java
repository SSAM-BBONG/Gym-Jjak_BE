package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.stub;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.TrainerApprovalUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainerApplicationUserStubAdapter implements TrainerApplicationUserPort {

    // TODO: 주원아 user 쪽에 adapter 구현되면 이 클래스 주석처라해줘. 머지하고 내가 지울게
    @Override
    public TrainerApprovalUserInfo promoteToTrainer(Long userId) {
        log.warn(
                "event=trainer_application_user_stub_promote_to_trainer, userId={}, reason=user_domain_adapter_not_implemented",
                userId
        );

        return new TrainerApprovalUserInfo(
                userId,
                "임시 트레이너",
                "stub-user@example.com",
                "임시 닉네임"
        );
    }
}
