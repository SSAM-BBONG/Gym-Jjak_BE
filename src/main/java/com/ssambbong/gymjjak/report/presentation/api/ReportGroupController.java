package com.ssambbong.gymjjak.report.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import com.ssambbong.gymjjak.report.application.usecase.ReportQueryUseCase;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import com.ssambbong.gymjjak.report.presentation.api.response.AdminReportListResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "신고그룹 컨트롤러", description = "관리자 신고그룹 관리 REST-API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReportGroupController {

//    private final ReportCommandUseCase reportCommandUseCase;

    private final ReportQueryUseCase reportQueryUseCase;

    @Operation(summary = "타입별 신고 그룹을 조회", description = "관리자가 targetType 기준으로 신고 그룹 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping("/reports")
    public ResponseEntity<GlobalApiResponse<AdminReportListResponse>> findReportGroups(
            @RequestParam ReportTargetType targetType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        AdminReportListQuery query = new AdminReportListQuery(targetType, page, size);

        AdminReportListResult result = reportQueryUseCase.findReportGroups(query);

        AdminReportListResponse response = AdminReportListResponse.from(result);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.GET_ADMIN_REPORT_LIST_SUCCESS,
                        response
                )
        );
    }
}
