package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.chat.ChatReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.community.CommentReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.community.PostReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.feedback.FeedbackReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.ptcourse.PtCourseReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetQueryPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.trainerreview.TrainerReviewReportTargetPort;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportTargetQueryAdapter implements ReportTargetQueryPort {

    private final PtCourseReportTargetPort ptCourseReportTargetPort;
    private final FeedbackReportTargetPort feedbackReportTargetPort;
    private final TrainerReviewReportTargetPort trainerReviewReportTargetPort;
    private final PostReportTargetPort postReportTargetPort;
    private final CommentReportTargetPort commentReportTargetPort;
    private final ChatReportTargetPort chatReportTargetPort;

    @Override
    public ReportTargetSnapshot getSnapshot(ReportTargetType targetType, Long targetId) {

        return switch (targetType) {
            case PT_COURSE -> ptCourseReportTargetPort.getSnapshot(targetId);
            case FEEDBACK -> feedbackReportTargetPort.getSnapshot(targetId);
            case TRAINER_REVIEW -> trainerReviewReportTargetPort.getSnapshot(targetId);
            case POST -> postReportTargetPort.getSnapshot(targetId);
            case COMMENT -> commentReportTargetPort.getSnapshot(targetId);
            case CHAT -> chatReportTargetPort.getSnapshot(targetId);
        };

    }
}
