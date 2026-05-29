package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.PtCourseSanctionPort;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionTargetPort;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportSanctionTargetAdapter implements ReportSanctionTargetPort {


    private final PtCourseSanctionPort ptCourseSanctionPort;
//    private final PostSanctionPort postSanctionPort;
//    private final CommentSanctionPort commentSanctionPort;

    @Override
    public void changeAutoBlind(ReportTargetType targetType, Long targetId, ReportSanctionAction action) {
        switch (targetType) {
            case PT_COURSE -> ptCourseSanctionPort.changeAutoBlind(targetId, action);
            case POST, COMMENT -> {
                // TODO: 게시글/댓글 자동 블라인드 적용/해제 포트 구현 후 연결
            }
            default -> {
                // TODO : FEEDBACK, TRAINER_REVIEW는 자동 제재 대상 아님, 추후 구현 예정
            }
        }
    }
}
