package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;

public interface TrainerApplicationReviewQueryUseCase {
    // 관리자 승인 관리 페이지 조회
    TrainerApplicationListResult findTrainerApplications(FindTrainerApplicationsCondition condition);

    // 관리자 트레이너 상세 조회
    TrainerApplicationReviewDetailResult getTrainerApplicationReviewDetail(Long trainerApplicationId);
}
