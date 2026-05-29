package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.PtCourseSanctionPort;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PtCourseSanctionAdapter implements PtCourseSanctionPort {

    @Override
    public void changeAutoBlind(Long targetId, ReportSanctionAction action) {
        // TODO 현지가 구현하면 삭제할 클래스
        // TODO : 해당 PT 게시글을 Action 상태에 따라 삭제 / 활성화 시켜주면 됨 void 여서 반환값 없어도 됨
    }
}