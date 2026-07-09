package com.ssambbong.gymjjak.report.infrastructure.stub;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.community.PostSanctionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostSanctionStubAdapter implements PostSanctionPort {

    @Override
    public void applySanction(Long targetId, ReportSanctionAction action) {
        log.warn("[PostSanctionStub] 게시글 제재 임시 처리 - postId={}, action={}", targetId, action);
    }
}
