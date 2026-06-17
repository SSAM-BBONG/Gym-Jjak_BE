package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;

import java.util.Optional;

public interface TrainerApplicationReviewQueryPort {

    // 관리자 심사용 목록 조회
    TrainerApplicationListResult findTrainerApplications(FindTrainerApplicationsCondition condition);

    // 관리자 트레이너 상세 조회
    Optional<TrainerApplicationReviewDetailResult> findTrainerApplicationReviewDetailById(Long trainerApplicationId);
}
