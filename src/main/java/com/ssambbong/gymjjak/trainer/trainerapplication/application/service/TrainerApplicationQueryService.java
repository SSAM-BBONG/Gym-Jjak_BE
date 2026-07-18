package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationQueryPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.TrainerApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerApplicationQueryService implements TrainerApplicationQueryUseCase {

    private static final int MY_TRAINER_APPLICATION_PAGE_SIZE = 10;

    private final TrainerApplicationQueryPort trainerApplicationQueryPort;

    @Override
    // 트레이너 신청서 목록 조회
    public MyTrainerApplicationListResult findMyTrainerApplications(Long requesterId, int page) {
        log.info("event=my_trainer_application_list_query_start, requesterId={}, page={}", requesterId, page);
        return trainerApplicationQueryPort.findMyTrainerApplications(
                requesterId, page, MY_TRAINER_APPLICATION_PAGE_SIZE
        );
    }

    @Override
    // 트레이너 신청서 상세 조회
    public TrainerApplicationDetailResult getMyTrainerApplication(
            Long requesterId,
            Long trainerApplicationId
    ) {
        log.info(
                "event=trainer_application_detail_query_start, requesterId={}, trainerApplicationId={}",
                requesterId,
                trainerApplicationId
        );

        TrainerApplicationDetailResult result = trainerApplicationQueryPort
                .findMyTrainerApplicationDetailById(requesterId, trainerApplicationId)
                .orElseThrow(() -> new TrainerApplicationNotFoundException(trainerApplicationId));

        log.info(
                "event=trainer_application_detail_query_succeeded, requesterId={}, trainerApplicationId={}, status={}",
                requesterId,
                result.trainerApplicationId(),
                result.status()
        );

        return result;
    }
}
