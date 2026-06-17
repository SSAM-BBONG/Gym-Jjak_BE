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

    @Override
    public TrainerApplicationListResult findTrainerApplications(FindTrainerApplicationsCondition condition) {
        // pageable 객체 생성
        PageRequest pageRequest = PageRequest.of(
                condition.page(),
                condition.size()
        );

        Page<TrainerApplicationSummaryResult> page =
                springDataTrainerApplicationRepository.findTrainerApplicationSummaries(
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

    @Override
    public Optional<TrainerApplicationReviewDetailResult> findTrainerApplicationReviewDetailById(Long trainerApplicationId) {
        return springDataTrainerApplicationRepository.findTrainerApplicationReviewDetailById(trainerApplicationId);
    }
}
