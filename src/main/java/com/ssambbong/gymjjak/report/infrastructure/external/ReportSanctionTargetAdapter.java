package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.chat.ChatSanctionPort;
import com.ssambbong.gymjjak.report.application.port.community.CommentSanctionPort;
import com.ssambbong.gymjjak.report.application.port.community.PostSanctionPort;
import com.ssambbong.gymjjak.report.application.port.feedback.FeedbackSanctionPort;
import com.ssambbong.gymjjak.report.application.port.ptcourse.PtCourseSanctionPort;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionTargetPort;
import com.ssambbong.gymjjak.report.application.port.trainerreview.TrainerReviewSanctionPort;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportSanctionTargetAdapter implements ReportSanctionTargetPort {


    private final PtCourseSanctionPort ptCourseSanctionPort;
    private final FeedbackSanctionPort feedbackSanctionPort;
    private final TrainerReviewSanctionPort trainerReviewSanctionPort;
    private final PostSanctionPort postSanctionPort;
    private final CommentSanctionPort commentSanctionPort;
    private final ChatSanctionPort chatSanctionPort;

    @Override
    public void applySanction(ReportTargetType targetType, Long targetId, ReportSanctionAction action) {

        log.info("[ReportInfrastructure] 외부 도메인 제재 실행 - targetType: {}, targetId: {}, action: {}",
                targetType, targetId, action);

        switch (targetType) {
            case PT_COURSE -> ptCourseSanctionPort.applySanction(targetId, action);

            case FEEDBACK -> {
                if (action == ReportSanctionAction.APPLY_MANUAL_BLIND) {
                    feedbackSanctionPort.applySanction(targetId, action);
                }
            }

            case TRAINER_REVIEW -> {
                if (action == ReportSanctionAction.APPLY_MANUAL_BLIND) {
                    trainerReviewSanctionPort.applySanction(targetId, action);
                }
            }
            case POST -> postSanctionPort.applySanction(targetId, action);

            case COMMENT -> commentSanctionPort.applySanction(targetId, action);

            case CHAT -> {
                if (action == ReportSanctionAction.APPLY_MANUAL_BLIND) {
                    chatSanctionPort.applySanction(targetId, action);
                }
            }
        }
    }
}
