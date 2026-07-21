package com.ssambbong.gymjjak.trainerReport.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainerReport.application.usecase.TrainerReportQueryUseCase;
import com.ssambbong.gymjjak.trainerReport.presentation.api.response.TrainerReportDetailResponse;
import com.ssambbong.gymjjak.trainerReport.presentation.api.response.TrainerReportListResponse;
import com.ssambbong.gymjjak.trainerReport.presentation.api.response.TrainerReportResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TrainerReport", description = "트레이너 시장동향 리포트 API")
@RestController
@RequestMapping("/api/trainer-reports")
@RequiredArgsConstructor
public class TrainerReportController {

    private final TrainerReportQueryUseCase trainerReportQueryUseCase;

    // 내 리포트 목록 조회 (트레이너 전용, 본인 것만)
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "내 트레이너 리포트 목록 조회", description = "트레이너가 본인의 시장동향 리포트 목록을 최신순으로 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TrainerReportListResponse.class))),
            @ApiResponse(responseCode = "403", description = "트레이너 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "트레이너 프로필 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<TrainerReportListResponse>> findMyReports(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        TrainerReportListResponse response = TrainerReportListResponse.from(
                trainerReportQueryUseCase.findMyReports(authUser.userId(), page, size));
        return ResponseEntity.ok(GlobalApiResponse.ok(TrainerReportResponseCode.TRAINER_REPORT_LIST, response));
    }

    // 내 리포트 상세 조회 (트레이너 전용, 본인 것만)
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "내 트레이너 리포트 상세 조회", description = "트레이너가 본인의 시장동향 리포트 상세(텍스트+시장 데이터 스냅샷)를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TrainerReportDetailResponse.class))),
            @ApiResponse(responseCode = "403", description = "트레이너 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "리포트 또는 트레이너 프로필을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{trainerReportId}")
    public ResponseEntity<GlobalApiResponse<TrainerReportDetailResponse>> findMyReportDetail(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long trainerReportId
    ) {
        TrainerReportDetailResponse response = TrainerReportDetailResponse.from(
                trainerReportQueryUseCase.findMyReportDetail(authUser.userId(), trainerReportId));
        return ResponseEntity.ok(GlobalApiResponse.ok(TrainerReportResponseCode.TRAINER_REPORT_DETAIL, response));
    }
}
