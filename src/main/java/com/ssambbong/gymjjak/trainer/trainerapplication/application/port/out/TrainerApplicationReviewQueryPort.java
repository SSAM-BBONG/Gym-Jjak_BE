package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;

public interface TrainerApplicationReviewQueryPort {

    // 관리자 심사용 목록 조회
    TrainerApplicationListResult findTrainerApplications(FindTrainerApplicationsCondition condition);
}
