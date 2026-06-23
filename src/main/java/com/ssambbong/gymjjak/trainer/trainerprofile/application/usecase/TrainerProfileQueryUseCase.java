package com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.MyTrainerProfileResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerProfileDetailResult;

public interface TrainerProfileQueryUseCase {

    // 트레이너 본인 프로필 조회
    MyTrainerProfileResult getMyTrainerProfile(Long requesterId);

    // 트레이너 프로필 상세 조회
    TrainerProfileDetailResult getTrainerProfileDetail(Long trainerProfileId);
}
