package com.ssambbong.gymjjak.dashboard.admin.presentation.api;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminMemberStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminPendingStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.usecase.AdminDashboardQueryUseCase;
import com.ssambbong.gymjjak.dashboard.admin.presentation.api.response.AdminDashboardResponseCode;
import com.ssambbong.gymjjak.dashboard.admin.presentation.api.response.AdminMemberStatisticsResponse;
import com.ssambbong.gymjjak.dashboard.admin.presentation.api.response.AdminPendingStatisticsResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "관리자 대시보드",
        description = "관리자 대시보드 API"
)
@RestController
@RequestMapping("/api/dashboard/admin")
@RequiredArgsConstructor
@Validated
public class AdminDashboardController {

    private final AdminDashboardQueryUseCase adminDashboardQueryUseCase;

    @GetMapping("/members")
    @Operation(
            summary = "관리자 대시보드 회원 현황 조회",
            description = "전체 이용자 수, 전체 트레이너 수, 전체 헬스장 수, 최근 6개월 월별 가입자 수 반환 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 대시보드 회원 현황 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse<AdminMemberStatisticsResponse>> findMemberStatistics() {

        AdminMemberStatisticsResult result =
                adminDashboardQueryUseCase.findMemberStatistics();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        AdminDashboardResponseCode.ADMIN_MEMBER_STATISTICS_FOUND,
                        AdminMemberStatisticsResponse.from(result)
                )
        );
    }

    @GetMapping("/approvals/pending")
    @Operation(
            summary = "관리자 대시보드 승인 대기 현황 조회",
            description = "트레이너 신청 대기 수와 조직 신청 대기 수 조회.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 대시보드 승인 대기 현황 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse<AdminPendingStatisticsResponse>> findPendingStatistics() {
        AdminPendingStatisticsResult result =
                adminDashboardQueryUseCase.findPendingStatistics();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        AdminDashboardResponseCode.ADMIN_PENDING_STATISTICS_FOUND,
                        AdminPendingStatisticsResponse.from(result)
                )
        );
    }
}
