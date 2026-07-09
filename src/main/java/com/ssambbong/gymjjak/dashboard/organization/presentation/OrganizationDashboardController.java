package com.ssambbong.gymjjak.dashboard.organization.presentation;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtCourseResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerClientResult;
import com.ssambbong.gymjjak.dashboard.organization.application.usecase.OrganizationDashboardUseCase;
import com.ssambbong.gymjjak.dashboard.organization.presentation.api.response.DashboardResponseCode;
import com.ssambbong.gymjjak.dashboard.organization.presentation.api.response.OrgPtCourseResponse;
import com.ssambbong.gymjjak.dashboard.organization.presentation.api.response.OrgStatsResponse;
import com.ssambbong.gymjjak.dashboard.organization.presentation.api.response.TrainerClientResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "조직 대시보드", description = "조직 대시보드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard/organization")
public class OrganizationDashboardController {

    private final OrganizationDashboardUseCase organizationDashboardUseCase;

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "헬스장 통계 조회", description = "조직 계정이 헬스장 트레이너 수, 누적 이용자 수, 현재 이용자 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = OrgStatsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/stats")
    public ResponseEntity<GlobalApiResponse<OrgStatsResponse>> getStats(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        OrgStatsResult result = organizationDashboardUseCase.getStats(authUser.userId());
        return ResponseEntity.ok(
                GlobalApiResponse.ok(DashboardResponseCode.ORG_STATS_FOUND, OrgStatsResponse.from(result))
        );
    }

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "조직 PT 목록 조회", description = "조직 소속 트레이너들의 PT 과목 목록과 현재 수강생 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = OrgPtCourseResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/pt-courses")
    public ResponseEntity<GlobalApiResponse<List<OrgPtCourseResponse>>> getPtCourses(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        List<OrgPtCourseResult> results = organizationDashboardUseCase.getPtCourses(authUser.userId());
        List<OrgPtCourseResponse> response = results.stream()
                .map(OrgPtCourseResponse::from)
                .toList();
        return ResponseEntity.ok(
                GlobalApiResponse.ok(DashboardResponseCode.ORG_PT_COURSES_FOUND, response)
        );
    }

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "트레이너별 수강생 목록 조회", description = "조직 소속 트레이너 목록을 누적 수강생 수 내림차순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TrainerClientResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/trainers/clients")
    public ResponseEntity<GlobalApiResponse<List<TrainerClientResponse>>> getTrainerClients(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        List<TrainerClientResult> results = organizationDashboardUseCase.getTrainerClients(authUser.userId());
        List<TrainerClientResponse> response = results.stream()
                .map(TrainerClientResponse::from)
                .toList();
        return ResponseEntity.ok(
                GlobalApiResponse.ok(DashboardResponseCode.TRAINER_CLIENTS_FOUND, response)
        );
    }
}
