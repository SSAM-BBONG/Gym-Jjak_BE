package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.feedback.FeedbackReportTargetPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackReportTargetAdapter implements FeedbackReportTargetPort {

    private static final String FEEDBACK_REPORT_TITLE = "피드백";
    private final FeedbackRepository feedbackRepository;
    private final TrainerProfileQueryPort trainerProfileQueryPort;

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.debug("[FeedbackSnapshot] feedbackId={}", targetId);

        Feedback feedback = feedbackRepository.findById(targetId)
                .orElseThrow(FeedbackNotFoundException::new);

        // trainerProfileId -> userId 조회
        Long targetOwnerId = trainerProfileQueryPort.findUserIdByTrainerProfileId(
                feedback.getTrainerProfileId()
        );

        log.info("[FeedbackSnapshot] feedbackId={}", targetId);

        return new ReportTargetSnapshot(
                feedback.getId(),
                targetOwnerId,
                FEEDBACK_REPORT_TITLE,
                feedback.getContent(),
                null // 파일 없음
        );
    }
}
