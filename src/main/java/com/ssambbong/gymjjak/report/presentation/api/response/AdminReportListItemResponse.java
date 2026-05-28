package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportListItem;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 신고관리 페이지 속 1 행에 들어갈 데이터 값들
 * @param reportNumber : 신고 번호 UUID 값
 * @param targetType : 신고 그룹 타입, ex) PT, 피드백, 게시글 등등
 * @param targetId : 피신고 게시글 pk값
 * @param targetDisplayText : 제목 부분에 들어갈 신고 타입별 제목
 * @param targetOwnerUsername : 피신고 게시글 작성자
 * @param reportedAt : 최근 신고일
 * @param reportCount : 총 신고 건수
 * @param status : 현재 신고 게시글의 상태, PENDING : 임시 블라인드 상태, RESOLVED : 해결됨, REJECTED : 반려
 * @param navigationType : 상세 보기 클릭시 modal창 / 해당 게시글 페이지로 이동 결정 전달 값
 */
@Builder
public record AdminReportListItemResponse(
        String reportNumber,
        String targetType,
        Long targetId,
        String targetDisplayText,
        String targetOwnerUsername,
        LocalDateTime reportedAt,
        int reportCount,
        String status,
        String navigationType
) {
    public static AdminReportListItemResponse from(AdminReportListItem item) {
        return AdminReportListItemResponse.builder()
                .reportNumber(item.reportNumber())
                .targetType(item.targetType().getDescription())
                .targetId(item.targetId())
                .targetDisplayText(item.targetDisplayText())
                .targetOwnerUsername(item.targetOwnerUsername())
                .reportedAt(item.reportedAt())
                .reportCount(item.reportCount())
                .status(item.status().getDescription())
                .navigationType(item.navigationType().name())
                .build();
    }
}
