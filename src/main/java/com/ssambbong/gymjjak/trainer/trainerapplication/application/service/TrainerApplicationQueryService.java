package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationQueryPort;
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

    private final TrainerApplicationQueryPort trainerApplicationQueryPort;

    @Override
    public TrainerApplicationDetailResult getMyTrainerApplication(Long requesterId) {
        log.info("event=trainer_application_detail_query_start, requesterId={}", requesterId);

        TrainerApplicationDetailResult result = trainerApplicationQueryPort.findLatestDetailByUserId(requesterId)
                .orElseThrow(() -> TrainerApplicationNotFoundException.byUserId(requesterId));

        log.info(
                "event=trainer_application_detail_query_succeeded, requesterId={}, trainerApplicationId={}, status={}",
                requesterId,
                result.trainerApplicationId(),
                result.status()
        );

        return result;
    }
}
