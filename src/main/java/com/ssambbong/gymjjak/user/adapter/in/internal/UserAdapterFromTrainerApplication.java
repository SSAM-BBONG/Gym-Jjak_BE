package com.ssambbong.gymjjak.user.adapter.in.internal;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.TrainerApprovalUserInfo;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.domain.model.User;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAdapterFromTrainerApplication implements TrainerApplicationUserPort {

    private final SpringDataUserRepository springDataUserRepository;

    private final UserPort userPort;

    /* Comment
    *   PO님 해당 메서드 stub으로 구현되어 있어서 트레이너 조회쪽 테스트가 안되서
    *   제가 수정했습니다!
    *   changeRole로 role값만 변경하고 저장해서 반환하는걸로 수정했습니다.
    *   기존 코드는 주석처리 했으니 확인 후에 주석 지워주시면 되겠습니다.
    *   -정수-
    * */
    @Override
    public TrainerApprovalUserInfo promoteToTrainer(Long userId) {
        User user = userPort.findById(userId)
                        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.changeRole(
                UserRole.TRAINER,
                LocalDateTime.now()
        );

        User promotedUser = userPort.save(user);

        log.info(
                "event=trainer_application_user_promoted, userId={}, role={}",
                promotedUser.getId(),
                promotedUser.getRole()
        );

//        log.warn(
//                "event=trainer_application_user_stub_promote_to_trainer, userId={}, reason=user_domain_adapter_not_implemented",
//                userId
//        );
//        UserJpaEntity user = springDataUserRepository.findById(userId)
//                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));


        return new TrainerApprovalUserInfo(
                promotedUser.getId(),
                promotedUser.getName(),
                promotedUser.getUsername(),
                promotedUser.getNickname()
        );
    }
}
