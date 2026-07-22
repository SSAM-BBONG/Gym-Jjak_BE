package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.adapter;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.trainerreview.TrainerReviewReportTargetPort;
import com.ssambbong.gymjjak.pt.trainerReview.domain.exception.TrainerReviewNotFoundException;
import com.ssambbong.gymjjak.pt.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.pt.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerReviewReportTargetAdapter implements TrainerReviewReportTargetPort {

    private static final String TRAINER_REVIEW_REPORT_TITLE = "강사평";
    private final TrainerReviewRepository trainerReviewRepository;

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.debug("[TrainerReviewSnapshot] trainerReviewId={}", targetId);

        TrainerReview trainerReview = trainerReviewRepository.findActiveById(targetId)
                .orElseThrow(TrainerReviewNotFoundException::new);

        log.info("[TrainerReviewSnapshot] trainerReviewId={}", targetId);

        return new ReportTargetSnapshot(
                trainerReview.getId(),
                trainerReview.getUserId(),
                TRAINER_REVIEW_REPORT_TITLE,
                trainerReview.getContent(),
                null // 파일 없음
        );
    }
}
