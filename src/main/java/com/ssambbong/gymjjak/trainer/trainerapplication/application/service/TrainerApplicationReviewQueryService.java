package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationOrganizationPort;
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
    private final TrainerApplicationOrganizationPort trainerApplicationOrganizationPort;

    // 트레이너 신청 목록 조회
    @Override
    public TrainerApplicationListResult findTrainerApplications(
            FindTrainerApplicationsCondition condition,
            Long organizationAccountId
    ) {
        // 조직 userId로 조직ID 가져오기
        Long organizationId =
                trainerApplicationOrganizationPort.findOrganizationIdByAccountId(
                        organizationAccountId
                );

        log.info(
                "event=trainer_application_review_list_query_start, organizationId={}, status={}, keywordPresent={}, page={}, size={}",
                organizationId,
                condition.status(),
                condition.keyword() != null,
                condition.page(),
                condition.size()
        );

        TrainerApplicationListResult result =
                trainerApplicationReviewQueryPort.findTrainerApplications(
                        condition,
                        organizationId
                );

        log.info(
                "event=trainer_application_review_list_query_succeeded, organizationId={}, status={}, keywordPresent={}, page={}, size={}, totalElements={}, returnedCount={}",
                organizationId,
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
    public TrainerApplicationReviewDetailResult getTrainerApplicationReviewDetail(
            Long trainerApplicationId,
            Long organizationAccountId
    ) {
        Long organizationId =
                trainerApplicationOrganizationPort.findOrganizationIdByAccountId(
                        organizationAccountId
                );

        log.info(
                "event=trainer_application_review_detail_query_start, trainerApplicationId={}, organizationId={}",
                trainerApplicationId,
                organizationId
        );

        TrainerApplicationReviewDetailResult result =
                trainerApplicationReviewQueryPort.findTrainerApplicationReviewDetailById(
                                trainerApplicationId,
                                organizationId
                        )
                        .orElseThrow(() ->
                                new TrainerApplicationNotFoundException(
                                        trainerApplicationId
                                )
                        );

        log.info(
                "event=trainer_application_review_detail_query_succeeded, trainerApplicationId={}, organizationId={}, userId={}, status={}",
                result.trainerApplicationId(),
                organizationId,
                result.userId(),
                result.status()
        );

        return result;
    }
}
