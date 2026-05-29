package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.application.policy.ReportTargetOwnerPolicy;
import com.ssambbong.gymjjak.report.application.port.UserProfileView;
import com.ssambbong.gymjjak.report.application.port.UserQueryPort;
import com.ssambbong.gymjjak.report.application.query.*;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportNavigationType;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
@RequiredArgsConstructor
public class ReportGroupRepositoryAdapter implements ReportGroupRepository {

    private final SpringDataReportGroupRepository reportGroupRepository;
    private final SpringDataReportRepository reportRepository;
    private final ReportTargetOwnerPolicy reportTargetOwnerPolicy;
    private final UserQueryPort userQueryPort;
    private final ReportGroupPersistenceMapper reportGroupPersistenceMapper;

    @Override
    public ReportGroup save(ReportGroup reportGroup) {
        ReportGroupJpaEntity entity = reportGroupPersistenceMapper.toEntity(reportGroup);
        ReportGroupJpaEntity savedEntity = reportGroupRepository.save(entity);
        return reportGroupPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportGroup> findById(Long reportGroupId) {
        return reportGroupRepository.findById(reportGroupId)
                .map(reportGroupPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    // 이건 aggregate를 조작하는 명령 메서드가 아님
    // 화면용 읽기 모델을 만드는 메서드임
    public AdminReportListResult findAdminReportList(AdminReportListQuery query) {

        // 페이지 요청 객체 생성
        // 페이지 번호, 데이터 개수
        PageRequest pageRequest = PageRequest.of(query.page() - 1, query.size());

        // if로 신고 그룹 존재 여부 조회

        // JPA repo 조회
        Page<ReportGroupJpaEntity> page = reportGroupRepository
                .findByTargetTypeAndSanctionStatusNot(
                        query.targetType(),
                        ReportGroupSanctionStatus.MANUAL_BLINDED,
                        pageRequest
                );

        // 조회 결과 전체 감싸는 객체
        return new AdminReportListResult(
                page.getContent().stream()
                        // 각 entity를 AdminReportListItem으로 변환
                        .map(entity -> {
                            String targetOwnerUsername = resolveTargetOwnerUsername(entity);
                            String targetDisplayText = resolveTargetDisplayText(entity);
                            LocalDateTime latestReportedAt = resolveLatestReportedAt(entity);

                            return toAdminReportListItem(
                                    entity,
                                    targetOwnerUsername,
                                    targetDisplayText,
                                    latestReportedAt
                            );
                        })
                        .toList(),
                query.page(), // 현재 페이지 번호
                page.getSize(), // 페이지 크기
                page.getTotalElements(), // 전체 신고 그룹 개수
                page.getTotalPages() // 전체 페이지 수
        );
    }

    // 상세 신고 목록 조립
    @Override
    @Transactional(readOnly = true)
    public AdminReportDetailResult findReportDetail(Long reportGroupId) {

        ReportGroupJpaEntity reportGroup = reportGroupRepository.findById(reportGroupId)
                .orElseThrow(() -> new ReportGroupNotFoundException(reportGroupId));

        List<AdminReportReasonItem> reports = reportRepository
                .findByReportGroupIdOrderByCreatedAtDesc(reportGroupId)
                .stream()
                .map(this::toAdminReportReasonItem)
                .toList();

        return new AdminReportDetailResult(
                reportGroup.getReportGroupId(),
                reportGroup.getReviewStatus(),
                reports
        );
    }

    private AdminReportReasonItem toAdminReportReasonItem(ReportJpaEntity entity) {

        String reporterUsername = userQueryPort.findUserProfile(entity.getReporterId())
                .map(UserProfileView::username)
                .orElse("알 수 없음");

        return new AdminReportReasonItem(
                entity.getReportId(),
                reporterUsername,
                entity.getReason(),
                entity.getDetail(),
                entity.getCreatedAt(),
                entity.getStatus()
        );
    }


    /* Comment
    *   역할 : 신고 대상자 username 추출
    *   - 조회 의도가 더 명확해짐
    *   - null/미존재/탈퇴 같은 표시 정책을 한 polocy 클래스에서 처리 가능
    *   - 나중에 “알 수 없음”, “탈퇴한 사용자”, “비공개 사용자” 같은 룰 추가 가능
     * */
    // TODO : 주원이 코드 구현하면 활성화
    private String resolveTargetOwnerUsername(ReportGroupJpaEntity entity) {
        return reportTargetOwnerPolicy.resolveUsername(entity.getTargetOwnerId());
    }

    private LocalDateTime resolveLatestReportedAt(ReportGroupJpaEntity entity) {
        return reportRepository.findTopByReportGroupIdOrderByCreatedAtDesc(entity.getReportGroupId())
                .map(ReportJpaEntity::getCreatedAt)
                .orElse(entity.getCreatedAt());
    }

    // 신고 대상 게시글 제목값 추출
    private String resolveTargetDisplayText(ReportGroupJpaEntity entity) {
        return switch (entity.getTargetType()) {
            case PT_COURSE -> resolvePtCourseDisplayText(entity);
            case POST -> resolvePostDisplayText(entity);
            case COMMENT -> resolveCommentDisplayText(entity);
            case FEEDBACK -> resolveFeedbackDisplayText(entity);
            case TRAINER_REVIEW -> resolveTrainerReviewDisplayText(entity);
        };
    }

    private String resolvePtCourseDisplayText(ReportGroupJpaEntity entity) {
        return fallbackSnapshot(entity, "PT 신고 대상");
    }
    private String resolvePostDisplayText(ReportGroupJpaEntity entity) {
        return fallbackSnapshot(entity, "게시글 신고 대상");
    }
    private String resolveCommentDisplayText(ReportGroupJpaEntity entity) {
        return fallbackSnapshot(entity, "댓글 신고 대상 게시글");
    }
    private String resolveFeedbackDisplayText(ReportGroupJpaEntity entity) {
        return fallbackSnapshot(entity, "피드백 신고 대상");
    }
    private String resolveTrainerReviewDisplayText(ReportGroupJpaEntity entity) {
        return fallbackSnapshot(entity, "강사평 신고 대상 PT");
    }

    // 스냅샷 텍스트 공통 처리 메서드
    /* Comment
    *   스냅샷 제목 존재 -> 제목 반환
    *   스냅샷 제목 x, 내용 존재 -> 내용 일부 반환
    *   둘다 없으면 기본 하드코딩값
    * */
    private String fallbackSnapshot(ReportGroupJpaEntity entity, String defaultText) {
        if (entity.getSnapshotTitle() != null && !entity.getSnapshotTitle().isBlank()) {
            return truncate(entity.getSnapshotTitle(), 30);
        }

        if (entity.getSnapshotContent() != null && !entity.getSnapshotContent().isBlank()) {
            return truncate(entity.getSnapshotContent(), 30);
        }

        return defaultText;
    }

    // 텍스트 길면 자르는 용
    private String truncate(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.length() <= maxLength
                ? value
                : value.substring(0, maxLength) + "...";
    }

    //  === toDTO ===
    // AdminReportListItem 조회용 DTO 조립 메서드
    private AdminReportListItem toAdminReportListItem(
            ReportGroupJpaEntity entity,
            String targetOwnerUsername,
            String targetDisplayText,
            LocalDateTime latestReportedAt
    ) {
        return new AdminReportListItem(
                entity.getReportNumber(),
                entity.getTargetType(),
                entity.getTargetId(),
                targetDisplayText,
                targetOwnerUsername,
                latestReportedAt,
                entity.getEffectiveReportCount(),
                entity.getReviewStatus(),
                resolveNavigationType(entity.getTargetType())
        );
    }

    // 상세보기 결정 메서드
    private ReportNavigationType resolveNavigationType(ReportTargetType targetType) {
        return switch (targetType) {
            case PT_COURSE, POST, FEEDBACK -> ReportNavigationType.PAGE;
            case COMMENT, TRAINER_REVIEW -> ReportNavigationType.MODAL;
        };
    }
}
