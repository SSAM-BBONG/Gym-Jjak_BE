package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;

public interface TrainerApplicationReviewQueryUseCase {
    // 관리자 승인 관리 페이지 조회
    TrainerApplicationListResult findTrainerApplications(FindTrainerApplicationsCondition condition);
}
