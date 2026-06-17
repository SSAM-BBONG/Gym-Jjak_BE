package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationReviewQueryPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationReviewQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.TrainerApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerApplicationReviewQueryService implements TrainerApplicationReviewQueryUseCase {

    private final TrainerApplicationReviewQueryPort trainerApplicationReviewQueryPort;

    // 트레이너 신청 목록 조회
    @Override
    public TrainerApplicationListResult findTrainerApplications(FindTrainerApplicationsCondition condition) {

        log.info(
                "event=trainer_application_review_list_query_start, status={}, keywordPresent={}, page={}, size={}",
                condition.status(),
                condition.keyword() != null,
                condition.page(),
                condition.size()
        );

        // db 에서 대기 중인 신청들 조회
        TrainerApplicationListResult result =
                trainerApplicationReviewQueryPort.findTrainerApplications(condition);

        log.info(
                "event=trainer_application_review_list_query_succeeded, status={}, keywordPresent={}, page={}, size={}, totalElements={}, returnedCount={}",
                condition.status(),
                condition.keyword() != null,
                condition.page(),
                condition.size(),
                result.totalElements(),
                result.content().size()
        );

        return result;
    }

    // 트레이너 신청 상세 조회
    @Override
    public TrainerApplicationReviewDetailResult getTrainerApplicationReviewDetail(Long trainerApplicationId) {
        log.info(
                "event=trainer_application_review_detail_query_start, trainerApplicationId={}",
                trainerApplicationId
        );

        TrainerApplicationReviewDetailResult result =
                trainerApplicationReviewQueryPort.findTrainerApplicationReviewDetailById(trainerApplicationId)
                        .orElseThrow(() -> new TrainerApplicationNotFoundException(trainerApplicationId));

        log.info(
                "event=trainer_application_review_detail_query_succeeded, trainerApplicationId={}, userId={}, status={}",
                result.trainerApplicationId(),
                result.userId(),
                result.status()
        );

        return result;
    }
}
