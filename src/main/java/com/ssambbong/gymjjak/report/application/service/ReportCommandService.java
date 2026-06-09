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
import com.ssambbong.gymjjak.report.infrastructure.metrics.ReportGroupMetric;
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

    private final ReportGroupMetric reportGroupMetric;


    @Override
    public void createReport(CreateReportCommand command) {

        log.info("[ReportCommand] 신고 생성 시작 - reporterId: {}, targetType: {}, targetId: {}",
                command.reporterId(), command.targetType(), command.targetId());

        // 1. 신고 스냅샷 가져오기
        log.debug("[ReportCommand] 타겟 스냅샷 조회 요청");
        ReportTargetSnapshot snapshot = getTargetSnapshot(command);

        // 2. 본인 신고 방지 검증
        validateNotSelfReport(command.reporterId(), snapshot.targetOwnerId());

        // 3, 기존 신고 그룹 조회
        Optional<ReportGroup> reportGroupOptional =
                reportGroupRepository.findByTargetTypeAndTargetId(
                        command.targetType(),
                        command.targetId()
                );

        ReportGroup reportGroup;
        ReportGroupSanctionStatus previousSanctionStatus;

        boolean createdNewReportGroup = false;

        // 이미 존재
        if (reportGroupOptional.isPresent()) {
            reportGroup = reportGroupOptional.get();
            log.debug("[ReportCommand] 기존 신고 그룹 존재 - reportGroupId: {}", reportGroup.getReportGroupId());

            // 4. 중복 신고 검증
            validateDuplicateReport(command.reporterId(), reportGroup.getReportGroupId());
            // 현재 제재 상태 저장
            previousSanctionStatus = reportGroup.getSanctionStatus();

            // 상태 변경은 도메인 객체에게 위임
            // 누적 신고 수, 유효 신고 수 증가, 상태 변경
            reportGroup.registerNewReport();
            // 검토 상태 재계산
            reportGroup.syncAutoSanctionStatus();
        } else {
            log.debug("[ReportCommand] 새로운 신고 그룹 생성 진행");
            // 새로운 신고 그룹 생성
            reportGroup = createNewReportGroup(command, snapshot);
            // 새로운 그룹 검토 상태 NONE으로
            previousSanctionStatus = ReportGroupSanctionStatus.NONE;
            // 자동 제재 대상인지 검증
            reportGroup.syncAutoSanctionStatus();
        }

        // 5. DB 저장! -> 여기서부터 인프라 계층으로 위임
        // 저장된 신고 그룹 id 가져오기
        ReportGroup savedReportGroup = reportGroupRepository.save(reportGroup);
        // 저장된 신고그룹 ID를 사용해 실제 개별 신고를 생성한다.
        Report report = createNewReport(command, savedReportGroup.getReportGroupId());
        // 신고 저장
        reportRepository.save(report);
        log.info("[ReportCommand] 신고 저장 완료 - reportId: {}, reportGroupId: {}",
                report.getReportId(), savedReportGroup.getReportGroupId());

        // 6. 자동 제재 여부 확인 및 실행
        applyAutoBlindIfNeeded(previousSanctionStatus, savedReportGroup);

        // 신고 생성 Metric 카운트
        if (createdNewReportGroup) {
            reportGroupMetric.countCreatedReportGroup();
        }
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
            log.warn("[ReportCommand] 본인 신고 시도 감지 - reporterId: {}", reporterId);
            throw new SelfReportNotAllowedException(reporterId);
        }
    }

    // 중복 신고 방지
    private void validateDuplicateReport(Long reporterId, Long reportGroupId) {
        boolean exists = reportRepository.existsByReporterIdAndReportGroupId(reporterId, reportGroupId);
        if (exists) {
            log.warn("[ReportCommand] 중복 신고 시도 감지 - reporterId: {}, reportGroupId: {}", reporterId, reportGroupId);
            throw new DuplicateReportException(reporterId, reportGroupId);
        }
    }

    private ReportGroup createNewReportGroup(
            CreateReportCommand command,
            ReportTargetSnapshot snapshot
    ) {
        String uniqueReportNumber = reportNumberGenerator.generate();
        log.debug("[ReportCommand] 신규 신고 번호 발급 완료: {}", uniqueReportNumber);

        return ReportGroup.create(
                uniqueReportNumber, // 발급받은 13자리 TSID 삽입
                command.targetType(),
                command.targetId(),
                snapshot.targetOwnerId(),
                snapshot.title(),
                snapshot.content(),
                snapshot.fileUrl(),
                LocalDateTime.now()
        );
    }

    private Report createNewReport(
            CreateReportCommand command, Long reportGroupId) {
        return Report.create(
                reportGroupId,
                command.reporterId(),
                command.reason(),
                command.detail(),
                LocalDateTime.now()
        );
    }


    /**
     *
     * @param previousSanctionStatus : 이전 상태
     * @param reportGroup : 신고 당한 신고 그룹
     */
    private void applyAutoBlindIfNeeded(
            ReportGroupSanctionStatus previousSanctionStatus,
            ReportGroup reportGroup
    ) {
        if (previousSanctionStatus == ReportGroupSanctionStatus.NONE
                && reportGroup.getSanctionStatus() == ReportGroupSanctionStatus.AUTO_BLINDED) {
            log.debug("[createReport] 신고 5회 등록! None -> Auto Blind 변경 요청 요청 발생, 신고 적용 그룹 ID : {}",
                    reportGroup.getReportGroupId());
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
