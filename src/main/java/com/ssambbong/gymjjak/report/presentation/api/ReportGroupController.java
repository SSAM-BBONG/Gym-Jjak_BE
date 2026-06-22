package com.ssambbong.gymjjak.report.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.ManualBlindReportGroupCommand;
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
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam ReportTargetType targetType,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        log.info("[ReportGroupController] 관리자 신고 목록 조회 API 호출 - adminId: {}, targetType: {}",
                authUser.userId(), targetType);

        log.debug("[ReportGroupController] 상세 조회 조건 - page: {}, size: {}", page, size);
        // 데이터 읽는 조회 역할 담당 Query 객체로 반환
        AdminReportListQuery query = new AdminReportListQuery(targetType, page, size);

        AdminReportListResult result = reportGroupQueryUseCase.findReportGroups(query);

        // app 반환 객체 -> 웹 reponse 객체로 변환
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

        log.info("[ReportGroupController] 관리자 신고 상세 조회 API 호출 - reportGroupId: {}", reportGroupId);

        // Usecase 계층 호출, 비즈니스 조회 결과 담기
        AdminReportDetailResult result = reportGroupQueryUseCase.findReportDetail(reportGroupId);

        // web response dto로 변환
        AdminReportDetailResponse response = AdminReportDetailResponse.from(result);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.GET_ADMIN_REPORT_DETAIL_SUCCESS,
                        response
                )
        );
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "신고 승인 처리 api", description = "관리자가 개별 신고 사유를 승인한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 승인 처리 성공"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음")
    })
    @PatchMapping("/{reportGroupId}/reports/{reportId}/approve")
    public ResponseEntity<GlobalApiResponse<AdminReportReasonItemResponse>> approveReport(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long reportGroupId, @PathVariable Long reportId) {

        log.info("[ReportGroupController] 관리자 개별 신고 승인 API 호출 - reportGroupId: {}, reportId: {}, adminId: {}",
                reportGroupId, reportId, authUser.userId());

        // usecase 계층으로 전달할 식별자 명령 DTO
        AdminReportReasonItem result = reportGroupCommandUseCase.approveReport(new ApproveReportCommand(
                reportGroupId, reportId, authUser.userId()
        ));

        // appli 계층의 result -> response DTO 생성
        AdminReportReasonItemResponse  response = AdminReportReasonItemResponse.from(result);

        log.info("[ReportGroupController] 관리자 개별 신고 승인 처리 성공 - reportId: {}, 해당 사유의 최종 상태: {}",
                reportId, response.status());

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

        log.info("[ReportGroupController] 관리자 개별 신고 반려 API 호출 - reportGroupId: {}, reportId: {}, adminId: {}",
                reportGroupId, reportId, authUser.userId());

        AdminReportReasonItem result = reportGroupCommandUseCase.rejectReport(new RejectReportCommand(
                reportGroupId, reportId, authUser.userId()
        ));

        AdminReportReasonItemResponse  response = AdminReportReasonItemResponse.from(result);

        log.info("[ReportGroupController] 관리자 개별 신고 반려 처리 성공 - reportId: {}, 해당 사유의 최종 상태: {}",
                reportId, response.status());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.REJECT_REPORT_SUCCESS,
                        response
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "신고 그룹 수동 블라인드 처리",
            description = "관리자가 특정 신고 그룹의 제재를 수동 블라인드로 확정하고 soft delete 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 그룹 수동 블라인드 처리 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 인자값"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "신고 그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "신고 그룹 soft delete 처리 실패")
    })
    @PatchMapping("/{reportGroupId}/manual-blind")
    public ResponseEntity<GlobalApiResponse<Void>> manuallyBlindReportGroup(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long reportGroupId
    ) {
        reportGroupCommandUseCase.manuallyBlindReportGroup(
                new ManualBlindReportGroupCommand(
                        reportGroupId,
                        authUser.userId())
                );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.MANUAL_BLIND_REPORT_GROUP_SUCCESS,
                        null
                )
        );
    }
}
