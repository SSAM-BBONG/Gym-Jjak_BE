package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationListResult;

public interface TrainerApplicationQueryUseCase {

    // 다중 조직 신청에 맞춰 본인 신청서를 페이지 목록으로 조회합니다.
    MyTrainerApplicationListResult findMyTrainerApplications(Long requesterId, int page);

    // 최신 신청서가 아닌 본인 소유의 특정 신청서를 상세 조회합니다.
    TrainerApplicationDetailResult getMyTrainerApplication(Long requesterId, Long trainerApplicationId);
}
