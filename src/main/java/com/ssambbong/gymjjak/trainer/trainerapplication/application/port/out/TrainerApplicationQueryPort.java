package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationListResult;

import java.util.Optional;

public interface TrainerApplicationQueryPort {

    // 헬스장name 포함 신청 목록 페이지 단위 조회
    MyTrainerApplicationListResult findMyTrainerApplications(Long userId, int page, int size);

    // 트레이너 신청서 상세 조회
    Optional<TrainerApplicationDetailResult> findMyTrainerApplicationDetailById(Long userId, Long trainerApplicationId);
}
