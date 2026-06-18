package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateApprovedTrainerProfileCommand;

public interface ApprovedTrainerProfilePort {

    /* Comment
    *   트레이너 신청 승인 후, 트레이너 프로필 생성하는 java repo
    *   아래 3개 테이블에 요청
    *   - trainer_profiles insert
    *   - trainer_certifications insert
    *   - trainer_awards insert
    *   return 값 : trainer_profile_id
    * */
    Long createApprovedTrainerProfile(CreateApprovedTrainerProfileCommand command);
}
