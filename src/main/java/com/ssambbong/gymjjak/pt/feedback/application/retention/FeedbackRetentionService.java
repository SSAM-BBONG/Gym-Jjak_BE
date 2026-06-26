package com.ssambbong.gymjjak.pt.feedback.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackRetentionService {

    public static final String JOB_NAME = "feedback-retention"; // Job 클래스에서 참조하는 이름 상수

    private final FeedbackRetentionProperties properties;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMediaRepository feedbackMediaRepository;

    @Transactional
    public RetentionJobResult hardDeleteExpiredFeedbacks(LocalDateTime now) {
        LocalDateTime threshold = properties.threshold(now);

        // 소프트딜리트된 지 periodDays 초과한 피드백 ID 배치 조회
        List<Long> candidateIds = feedbackRepository.findHardDeleteCandidateIds(threshold, properties.batchSize());

        if (candidateIds.isEmpty()) {
            log.info("event=feedback-retention-empty threshold={}", threshold);
            return RetentionJobResult.empty(JOB_NAME);
        }

        // FK 제약 방지: 미디어 먼저 삭제 후 피드백 삭제
        int deletedMedia = feedbackMediaRepository.hardDeleteByFeedbackIds(candidateIds);
        int deletedFeedbacks = feedbackRepository.hardDeleteByIds(candidateIds);

        log.info("event=feedback-retention-completed threshold={}, candidateCount={}, deletedMedia={}, deletedFeedbacks={}",
                threshold, candidateIds.size(), deletedMedia, deletedFeedbacks);

        // deletedChildCount = 미디어 삭제 수
        return new RetentionJobResult(JOB_NAME, candidateIds.size(), deletedMedia, deletedFeedbacks);
    }
}
