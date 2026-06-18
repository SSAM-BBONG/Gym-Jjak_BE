package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.TrainerApprovalUserInfo;

public interface TrainerApplicationUserPort {

    /* Comment
    *   트레이너 신청 승인 시, User 도메인에 요청하는 기능
    *   역할
    *   1. user의 role TRAINER로 변경
    *   2. 트레이너 프로필 테이블에 트레이너 이름으로 저장할 name값 포함하여 반환
    * */
    TrainerApprovalUserInfo promoteToTrainer(Long userId);
}
