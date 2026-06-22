package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.application.policy.ReportTargetOwnerPolicy;
import com.ssambbong.gymjjak.report.application.port.UserProfileView;
import com.ssambbong.gymjjak.report.application.port.UserQueryPort;
import com.ssambbong.gymjjak.report.application.query.*;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.*;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ReportGroupRepositoryAdapter implements ReportGroupRepository {

    private final SpringDataReportGroupRepository reportGroupRepository;
    private final SpringDataReportRepository reportRepository;
    private final ReportTargetOwnerPolicy reportTargetOwnerPolicy;
    private final UserQueryPort userQueryPort;
    private final ReportGroupPersistenceMapper reportGroupPersistenceMapper;

    @Override
    public ReportGroup save(ReportGroup reportGroup) {
        // 엔티티로 변환
        ReportGroupJpaEntity entity = reportGroupPersistenceMapper.toEntity(reportGroup);

        // id 존재하는 경우 기존 엔티티에 상태 변경
        if (reportGroup.getReportGroupId() != null) {
            ReportGroupJpaEntity existing = reportGroupRepository.findById(reportGroup.getReportGroupId())
                    .orElseThrow(() -> new ReportGroupNotFoundException(reportGroup.getReportGroupId()));

            // 엔티티 -> 도메인 객체로 갱신
            existing.updateFromDomain(reportGroup);
            // 여기서 save 호출 안해도 트렌잭션에 의해 자동 변경 감지함!
            return reportGroupPersistenceMapper.toDomain(existing);
        }
        // 신규 저장
        ReportGroupJpaEntity saved = reportGroupRepository.save(entity);
        return reportGroupPersistenceMapper.toDomain(saved);
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

        log.debug("[ReportInfrastructure] DB 조회 실행 - targetType: {}, page: {}, size: {}",
                query.targetType(), query.page(), query.size());

        // 페이지 요청 객체 생성
        PageRequest pageRequest = PageRequest.of(query.page(), query.size());

        // JPA repo 조회
        // 수동 블라인드 처리된 그룹은 목록에서 제외하는 필터링
        Page<ReportGroupJpaEntity> page = reportGroupRepository
                .findByTargetTypeAndSanctionStatusNot(
                        query.targetType(),
                        ReportGroupSanctionStatus.MANUAL_BLINDED,
                        pageRequest
                );

        log.debug("[ReportInfrastructure] DB 조회 완료 - 조회된 Entity 개수: {}", page.getContent().size());

        // TODO : 만약 신고목록 10개 일때, 최신일 조회 10번, 신고자 조회 10번 총 21번 조회중
        //  추후 DTOProjection 사용해서 줄여서 개선사항으로 남기자
        // 조회 결과 전체 감싸는 반복 매핑
        List<AdminReportListItem> items = page.getContent().stream()
                .map(entity -> {
                    // 정책 컴포넌트 사용해서 피신고자 Username 조회
                    String targetOwnerUsername = resolveTargetOwnerUsername(entity);
                    // 스냅샷 기반 화면용 텍스트 담기
                    String targetDisplayText = resolveTargetDisplayText(entity);
                    // 가장 최근 신고 접수일 담기
                    LocalDateTime latestReportedAt = resolveLatestReportedAt(entity);

                    // 조회 DTO 반환
                    return toAdminReportListItem(
                            entity,
                            targetOwnerUsername,
                            targetDisplayText,
                            latestReportedAt
                    );
                })
                .toList();
        // appli 계층으로 넘기는 반환 값
        return new AdminReportListResult(
                items,
                query.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    // 상세 신고 목록 조립
    @Override
    @Transactional(readOnly = true)
    public AdminReportDetailResult findReportDetail(Long reportGroupId) {

        log.debug("[ReportInfrastructure] 상세 조회 DB 요청 - reportGroupId: {}", reportGroupId);

        // jpa 신고 그룹 조회
        // 존재x -> 도메인 예외 발생
        ReportGroupJpaEntity reportGroup = reportGroupRepository.findById(reportGroupId)
                .orElseThrow(() -> new ReportGroupNotFoundException(reportGroupId));

        // 해당 신고 그룹에 묶여있는 모든 개별 신고 내역 리스트 최신순 조회
        List<AdminReportReasonItem> reports = reportRepository
                .findByReportGroupIdOrderByCreatedAtDesc(reportGroupId)
                .stream()
                // jpaEntity 돌면서 Item DTO로 매핑
                .map(this::toAdminReportReasonItem)
                .toList();

        log.debug("[ReportInfrastructure] 엔티티 로드 완료 - 그룹 번호: {}, 연관 신고 건수: {}건",
                reportGroup.getReportNumber(), reports.size());

        log.info("[ReportInfrastructure] 최종 상세 DTO 조립 완료 - reportGroupId: {}", reportGroupId);

        // appli 계층으로 반환할 result 생성
        return new AdminReportDetailResult(
                reportGroup.getReportGroupId(),
                reportGroup.getReviewStatus(),
                reports // 개별 신고 사유 리스트
        );
    }

    // 개별 신고 엔티티 조회용 toDTO 매핑 메서드
    private AdminReportReasonItem toAdminReportReasonItem(ReportJpaEntity entity) {

        // TODO : N+1 문제 발생하는거 해결해보기! - 매번 reporterUsername 호출함
        // 유저 AggreGate로 조회port 호출 하여, 아이디 받기
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

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportGroup> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId) {
        return reportGroupRepository.findByTargetTypeAndTargetId(targetType, targetId)
                .map(reportGroupPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByReportNumber(String reportNumber) {
        return reportGroupRepository.existsByReportNumber(reportNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus reportGroupReviewStatus) {
        return reportGroupRepository.countByReviewStatusAndDeletedAtIsNull(reportGroupReviewStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBySanctionStatusAndDeletedAtIsNull(ReportGroupSanctionStatus reportGroupSanctionStatus) {
        return reportGroupRepository.countBySanctionStatusAndDeletedAtIsNull(reportGroupSanctionStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllByDeletedAtIsNull() {
        return reportGroupRepository.countAllByDeletedAtIsNull();
    }

    @Override
    public int softDeleteResolvedManualBlindedById(Long reportGroupId, LocalDateTime deletedAt) {
        return reportGroupRepository.softDeleteResolvedManualBlindedById(
                reportGroupId,
                ReportGroupReviewStatus.RESOLVED,
                ReportGroupSanctionStatus.MANUAL_BLINDED,
                deletedAt
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findManualBlindedResolvedHardDeleteCandidateIds(
            LocalDateTime threshold, int batchSize) {
        return reportGroupRepository.findManualBlindedResolvedHardDeleteCandidateIds(
                ReportGroupReviewStatus.RESOLVED,
                ReportGroupSanctionStatus.MANUAL_BLINDED,
                threshold,
                PageRequest.of(0, batchSize)
        );
    }

    @Override
    public int hardDeleteByIds(List<Long> reportGroupIds) {
        if (reportGroupIds == null || reportGroupIds.isEmpty()) {
            return 0;
        }

        return reportGroupRepository.hardDeleteByIds(reportGroupIds);
    }


    /* Comment
    *   역할 : 신고 대상자 username 추출
    *   - 조회 의도가 더 명확해짐
    *   - null/미존재/탈퇴 같은 표시 정책을 한 polocy 클래스에서 처리 가능
    *   - 나중에 “알 수 없음”, “탈퇴한 사용자”, “비공개 사용자” 같은 룰 추가 가능
     * */
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
            case PT_COURSE -> fallbackSnapshot(entity, "PT 강좌");
            case POST -> fallbackSnapshot(entity, "게시글");
            case COMMENT -> fallbackSnapshot(entity, "댓글");
            case FEEDBACK -> fallbackSnapshot(entity, "피드백");
            case TRAINER_REVIEW -> fallbackSnapshot(entity, "트레이너 리뷰");
        };
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
                entity.getReportGroupId(),
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
