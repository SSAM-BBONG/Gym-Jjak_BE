package com.ssambbong.gymjjak.pt.trainerReview.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.pt.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerReviewRetentionService {

    public static final String JOB_NAME = "trainer-review-retention";

    private final TrainerReviewRetentionProperties properties;
    private final TrainerReviewRepository trainerReviewRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredTrainerReviews(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);

        List<Long> candidateIds = trainerReviewRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=trainer-review-retention-empty threshold={}", threshold);
            return RetentionJobResult.empty(JOB_NAME);
        }

        int deleted = trainerReviewRepository.hardDeleteByIds(candidateIds);

        log.info("event=trainer-review-retention-completed threshold={}, candidateCount={}, deleted={}",
                threshold, candidateIds.size(), deleted);

        return new RetentionJobResult(JOB_NAME, candidateIds.size(), 0, deleted);
    }
}
