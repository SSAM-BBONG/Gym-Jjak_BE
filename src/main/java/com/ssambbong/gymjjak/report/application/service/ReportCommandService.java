package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionTargetPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetQueryPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.usecase.CreateReportCommand;
import com.ssambbong.gymjjak.report.application.usecase.ReportCommandUseCase;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReportCommandService implements ReportCommandUseCase {

    private final ReportRepository reportRepository;
    private final ReportGroupRepository reportGroupRepository;
    private final ReportTargetQueryPort reportTargetQueryPort;
    private final ReportSanctionTargetPort reportSanctionTargetPort;
//    private final ReportNumberGenerator reportNumberGenerator;

    @Override
    public Long createReport(CreateReportCommand command) {

        log.info("[ReportCommandService] 신고 생성 시작!");
        // 신고 스냅샷 저장하기
        ReportTargetSnapshot snapshot = getTargetSnapshot(command);

        Optional<ReportGroup> reportGroupOptional =
                reportGroupRepository.findByTargetTypeAndTargetId(
                        command.targetType(),
                        command.targetId()
                );
        // 신고 대상 존재 여부 검증


        // 본인 게시글 신고 방지 검증
//        validatedNotSelfReport(command.reporterId(), snapshot.targetOwnerId());

        // 중복 신고 방지 검증

        // ReportGroup 없으면 새로 생성, 있으면 기존 거에 올리기

        // report 생성

        // 그룹 count 증가

        // 그룹 리뷰 상태 관리 이동시키기?
        return 0L;
    }

    private ReportTargetSnapshot getTargetSnapshot(CreateReportCommand command) {
        return reportTargetQueryPort.getSnapshot(
                command.targetType(),
                command.targetId()
        );
    }
}
