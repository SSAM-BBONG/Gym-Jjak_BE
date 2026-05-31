package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportListItem;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 신고관리 페이지 속 1 행에 들어갈 데이터 값들
 * @param reportGroupId : 신고 당한 글 pk값
 * @param reportNumber : 신고 번호 UUID 값
 * @param targetType : 신고 그룹 타입, ex) PT, 피드백, 게시글 등등
 * @param targetId : 피신고 게시글 pk값
 * @param targetDisplayText : 제목 부분에 들어갈 신고 타입별 제목
 * @param targetOwnerUsername : 피신고 게시글 작성자
 * @param reportedAt : 최근 신고일
 * @param effectiveReportCount : 승인된 신고 건수
 * @param status : 현재 신고 게시글의 상태, PENDING : 대기상태, RESOLVED : 검토 완료, REJECTED : 검토 완료, 모든 승인 반려
 * @param navigationType : 상세 보기 클릭시 modal창 / 해당 게시글 페이지로 이동 결정 전달 값
 */
@Builder
public record AdminReportListItemResponse(
        Long reportGroupId,
        String reportNumber,
        String targetType,
        Long targetId,
        String targetDisplayText,
        String targetOwnerUsername,
        LocalDateTime reportedAt,
        int effectiveReportCount,
        String status,
        String navigationType
) {
    public static AdminReportListItemResponse from(AdminReportListItem item) {
        return AdminReportListItemResponse.builder()
                .reportGroupId(item.reportGroupId())
                .reportNumber(item.reportNumber())
                .targetType(item.targetType().getDescription())
                .targetId(item.targetId())
                .targetDisplayText(item.targetDisplayText())
                .targetOwnerUsername(item.targetOwnerUsername())
                .reportedAt(item.reportedAt())
                .effectiveReportCount(item.effectiveReportCount())
                .status(item.status().getDescription())
                .navigationType(item.navigationType().name())
                .build();
    }
}
