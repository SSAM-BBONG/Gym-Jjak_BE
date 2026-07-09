package com.ssambbong.gymjjak.report.infrastructure.stub;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.community.CommentSanctionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentSanctionStubAdapter implements CommentSanctionPort {

    @Override
    public void applySanction(Long targetId, ReportSanctionAction action) {
        log.warn("[CommentSanctionStub] 댓글 제재 임시 처리 - commentId={}, action={}", targetId, action);
    }
}
