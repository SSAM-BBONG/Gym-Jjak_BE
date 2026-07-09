package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationReviewQueryPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 관리자 심사용 목록 read model 조회
@Repository
@RequiredArgsConstructor
public class TrainerApplicationReviewQueryAdapter implements TrainerApplicationReviewQueryPort {

    private final SpringDataTrainerApplicationRepository springDataTrainerApplicationRepository;

    // 조직별 트레이너 신청 목록 조회 기능
    @Override
    public TrainerApplicationListResult findTrainerApplications(
            FindTrainerApplicationsCondition condition,
            Long organizationId
    ) {
        PageRequest pageRequest = PageRequest.of(
                condition.page(),
                condition.size()
        );

        Page<TrainerApplicationSummaryResult> page =
                springDataTrainerApplicationRepository.findTrainerApplicationSummaries(
                        organizationId,
                        condition.status(),
                        condition.keyword(),
                        pageRequest
                );

        return new TrainerApplicationListResult(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }

    // 조직별 트레이너 신청 상세 조회 기능
    @Override
    public Optional<TrainerApplicationReviewDetailResult> findTrainerApplicationReviewDetailById(
            Long trainerApplicationId,
            Long organizationId
    ) {
        return springDataTrainerApplicationRepository.findTrainerApplicationReviewDetailById(
                trainerApplicationId,
                organizationId
        );
    }
}
