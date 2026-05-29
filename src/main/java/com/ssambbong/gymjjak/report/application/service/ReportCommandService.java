package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.policy.ReportNumberGenerator;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionTargetPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetQueryPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.usecase.CreateReportCommand;
import com.ssambbong.gymjjak.report.application.usecase.ReportCommandUseCase;
import com.ssambbong.gymjjak.report.domain.exception.DuplicateReportException;
import com.ssambbong.gymjjak.report.domain.exception.SelfReportNotAllowedException;
import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ReportNumberGenerator reportNumberGenerator;

    @Override
    public void createReport(CreateReportCommand command) {

        log.info("[ReportCommandService] 신고 생성 시작!");
        // 신고 스냅샷 저장
        // 신고 대상 존재 여부 검증
        ReportTargetSnapshot snapshot = getTargetSnapshot(command);

        // 본인 신고 방지
        validateNotSelfReport(command.reporterId(), snapshot.targetOwnerId());

        // 기존에 생성된 신고그룹 존재 여부 검증
        Optional<ReportGroup> reportGroupOptional =
                reportGroupRepository.findByTargetTypeAndTargetId(
                        command.targetType(),
                        command.targetId()
                );

        ReportGroup reportGroup;
        ReportGroupSanctionStatus previousSanctionStatus;

        // 이미 존재
        if (reportGroupOptional.isPresent()) {
            reportGroup = reportGroupOptional.get();

            // 중복 신고 검증
            validateDuplicateReport(command.reporterId(), reportGroup.getReportGroupId());

            // 현재 제재 상태 저장
            previousSanctionStatus = reportGroup.getSanctionStatus();

            // 누적 신고 수, 유효 신고 수 증가
            // 검토 상태 Pending 상태로 변경
            reportGroup.registerNewReport();

            // 검토 상태 재계산
            reportGroup.syncAutoSanctionStatus();
        } else {
            // 새로운 신고 그룹 생성
            reportGroup = createNewReportGroup(command, snapshot);
            // 새로운 그룹 검토 상태 NONE으로
            previousSanctionStatus = ReportGroupSanctionStatus.NONE;
            // 자동 제재 대상인지 검증
            reportGroup.syncAutoSanctionStatus();
        }

        // 저장된 신고 그룹 id 가져오기
        ReportGroup savedReportGroup = reportGroupRepository.save(reportGroup);

        // 저장된 신고그룹 ID를 사용해 실제 개별 신고를 생성한다.
        Report report = createNewReport(command, savedReportGroup.getReportGroupId());

        // 신고 저장
        reportRepository.save(report);

        applyAutoBlindIfNeeded(previousSanctionStatus, savedReportGroup);


    }

    private ReportTargetSnapshot getTargetSnapshot(CreateReportCommand command) {
        return reportTargetQueryPort.getSnapshot(
                command.targetType(),
                command.targetId()
        );
    }

    // 본인 신고 방지
    private void validateNotSelfReport(Long reporterId, Long targetOwnerId) {
        if (reporterId.equals(targetOwnerId)) {
            throw new SelfReportNotAllowedException(reporterId);
        }
    }

    // 중복 신고 검증
    private void validateDuplicateReport(Long reporterId, Long reportGroupId) {
        boolean exists = reportRepository.existsByReporterIdAndReportGroupId(reporterId, reportGroupId);

        if (exists) {
            throw new DuplicateReportException(reporterId, reportGroupId);
        }
    }

    private ReportGroup createNewReportGroup(
            CreateReportCommand command,
            ReportTargetSnapshot snapshot
    ) {
        return ReportGroup.create(
                generateUniqueReportNumber(),
                command.targetType(),
                command.targetId(),
                snapshot.targetOwnerId(),
                snapshot.title(),
                snapshot.content(),
                snapshot.fileUrl(),
                LocalDateTime.now()
        );
    }

    private Report createNewReport(CreateReportCommand command, Long reportGroupId) {
        return Report.create(
                reportGroupId,
                command.reporterId(),
                command.reason(),
                command.detail(),
                LocalDateTime.now()
        );
    }

    private void applyAutoBlindIfNeeded(
            ReportGroupSanctionStatus previousSanctionStatus,
            ReportGroup reportGroup
    ) {
        if (previousSanctionStatus == ReportGroupSanctionStatus.NONE
                && reportGroup.getSanctionStatus() == ReportGroupSanctionStatus.AUTO_BLINDED) {
            reportSanctionTargetPort.changeAutoBlind(
                    reportGroup.getTargetType(),
                    reportGroup.getTargetId(),
                    ReportSanctionAction.APPLY_AUTO_BLIND
            );
        }
    }

    private String generateUniqueReportNumber() {
        String reportNumber;
        do {
            reportNumber = reportNumberGenerator.generate();
        } while (reportGroupRepository.existsByReportNumber(reportNumber));
        return reportNumber;
    }
}
