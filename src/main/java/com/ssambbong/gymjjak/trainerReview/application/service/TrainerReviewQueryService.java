package com.ssambbong.gymjjak.trainerReview.application.service;

import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListQuery;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListResult;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewSummary;
import com.ssambbong.gymjjak.trainerReview.application.usecase.TrainerReviewQueryUseCase;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainerReviewQueryService implements TrainerReviewQueryUseCase {

    private final TrainerReviewRepository trainerReviewRepository;

    @Override
    @Transactional(readOnly = true)
    public TrainerReviewSummary getSummary(Long trainerProfileId) {
        return trainerReviewRepository.findSummary(trainerProfileId);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerReviewListResult getReviews(TrainerReviewListQuery query) {
        return trainerReviewRepository.findList(query);
    }
}
