package com.ssambbong.gymjjak.report.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.RejectReportCommand;
import com.ssambbong.gymjjak.report.application.query.*;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupCommandUseCase;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupQueryUseCase;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import com.ssambbong.gymjjak.report.presentation.api.response.AdminReportDetailResponse;
import com.ssambbong.gymjjak.report.presentation.api.response.AdminReportListResponse;
import com.ssambbong.gymjjak.report.presentation.api.response.AdminReportReasonItemResponse;
import com.ssambbong.gymjjak.report.presentation.api.response.ReportResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report_Group", description = "관리자 신고그룹 관리 REST-API")
@RestController
@RequestMapping("/api/reportgroup")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReportGroupController {

    private final ReportGroupCommandUseCase reportGroupCommandUseCase;

    private final ReportGroupQueryUseCase reportGroupQueryUseCase;

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "타입별 신고 그룹을 조회", description = "관리자가 targetType 기준으로 신고 그룹 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping("/list")
    public ResponseEntity<GlobalApiResponse<AdminReportListResponse>> findReportGroups(
            @RequestParam ReportTargetType targetType,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        AdminReportListQuery query = new AdminReportListQuery(targetType, page, size);

        AdminReportListResult result = reportGroupQueryUseCase.findReportGroups(query);

        AdminReportListResponse response = AdminReportListResponse.from(result);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.GET_ADMIN_REPORT_LIST_SUCCESS,
                        response
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "신고 사유 상세 조회", description = "관리자가 특정 신고 그룹의 신고 사유 상세 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 사유 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "신고 그룹을 찾을 수 없음")
    })
    @GetMapping("/detail/{reportGroupId}")
    public ResponseEntity<GlobalApiResponse<AdminReportDetailResponse>> findReportDetails(@PathVariable Long reportGroupId) {

        AdminReportDetailResult result = reportGroupQueryUseCase.findReportDetail(reportGroupId);

        AdminReportDetailResponse response = AdminReportDetailResponse.from(result);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.GET_ADMIN_REPORT_DETAIL_SUCCESS,
                        response
                )
        );
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "신고 승인 처리", description = "관리자가 특정 신고를 승인한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 승인 처리 성공"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음")
    })
    @PatchMapping("/{reportGroupId}/reports/{reportId}/approve")
    public ResponseEntity<GlobalApiResponse<AdminReportReasonItemResponse>> approveReport(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long reportGroupId, @PathVariable Long reportId) {

        AdminReportReasonItem result = reportGroupCommandUseCase.approveReport(new ApproveReportCommand(
                reportGroupId, reportId, authUser.userId()
        ));

        AdminReportReasonItemResponse  response = AdminReportReasonItemResponse.from(result);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.APPROVE_REPORT_SUCCESS,
                        response
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "신고 반려 처리", description = "관리자가 특정 신고를 반려한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 반려 처리 성공"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음")
    })
    @PatchMapping("/{reportGroupId}/reports/{reportId}/reject")
    public ResponseEntity<GlobalApiResponse<AdminReportReasonItemResponse>> rejectReport(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long reportGroupId, @PathVariable Long reportId) {

        AdminReportReasonItem result = reportGroupCommandUseCase.rejectReport(new RejectReportCommand(
                reportGroupId, reportId, authUser.userId()
        ));

        AdminReportReasonItemResponse  response = AdminReportReasonItemResponse.from(result);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.REJECT_REPORT_SUCCESS,
                        response
                )
        );
    }
}
