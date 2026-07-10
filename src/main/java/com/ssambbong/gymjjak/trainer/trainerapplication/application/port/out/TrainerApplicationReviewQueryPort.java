package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;

import java.util.Optional;

public interface TrainerApplicationReviewQueryPort {

    // 조직별 트레이너 신청 목록 조회 기능
    TrainerApplicationListResult findTrainerApplications(
            FindTrainerApplicationsCondition condition,
            Long organizationId
    );

    // 조직별 트레이너 신청 상세 조회 기능
    Optional<TrainerApplicationReviewDetailResult> findTrainerApplicationReviewDetailById(
            Long trainerApplicationId,
            Long organizationId
    );
}
