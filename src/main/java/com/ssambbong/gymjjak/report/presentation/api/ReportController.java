package com.ssambbong.gymjjak.report.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import com.ssambbong.gymjjak.global.security.principal.AuthUser;
import com.ssambbong.gymjjak.report.application.usecase.CreateReportCommand;
import com.ssambbong.gymjjak.report.application.usecase.ReportCommandUseCase;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupCommandUseCase;
import com.ssambbong.gymjjak.report.presentation.api.request.CreateReportRequest;
import com.ssambbong.gymjjak.report.presentation.api.response.CreateReportResponse;
import com.ssambbong.gymjjak.report.presentation.api.response.ReportResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "report", description = "신고 기능 REST-API")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReportController {

    private final ReportCommandUseCase reportCommandUseCase;

    @Operation(
            summary = "신고 접수",
            description = "회원이 특정 대상(PT, 게시글, 댓글, 피드백, 강사평)을 신고한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 접수 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청이거나 본인 신고/중복 신고인 경우"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "신고 대상이 존재하지 않는 경우")
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<Void>> createReport(
            @Valid @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal AuthUser authUser
            ) {
        reportCommandUseCase.createReport(
                new CreateReportCommand(
                        authUser.userId(),
                        request.targetId(),
                        request.targetType(),
                        request.reason(),
                        request.detail()
                ));

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ReportResponseCode.CREATE_REPORT_SUCCESS.getCode(),
                        ReportResponseCode.CREATE_REPORT_SUCCESS.getMessage()
                )
        );
    }

}
