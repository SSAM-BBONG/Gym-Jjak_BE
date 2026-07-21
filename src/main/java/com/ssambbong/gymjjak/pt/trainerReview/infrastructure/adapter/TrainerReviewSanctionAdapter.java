package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.trainerReview.domain.exception.TrainerReviewNotFoundException;
import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.pt.trainerReview.domain.repository.TrainerReviewRepository;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.trainerreview.TrainerReviewSanctionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerReviewSanctionAdapter implements TrainerReviewSanctionPort {

    private final TrainerReviewRepository trainerReviewRepository;
    private final Clock clock;

    @Override
    @Transactional
    public void applySanction(Long targetId, ReportSanctionAction action) {
        log.debug("[TrainerReviewSanction] trainerReviewId={}, action={}", targetId, action);

        TrainerReview trainerReview = trainerReviewRepository.findActiveById(targetId)
                .orElseThrow(TrainerReviewNotFoundException::new);

        switch (action) {
            case APPLY_MANUAL_BLIND -> trainerReviewRepository.save(trainerReview.delete(LocalDateTime.now(clock)));
        }

        log.info("[TrainerReviewSanction] trainerReviewId={}, action={}", targetId, action);
    }
}
