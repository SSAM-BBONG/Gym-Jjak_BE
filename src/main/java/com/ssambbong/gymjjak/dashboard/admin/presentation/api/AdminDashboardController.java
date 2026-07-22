package com.ssambbong.gymjjak.dashboard.admin.presentation.api;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminContentStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminMemberStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminRevenueStatisticsResult;
import com.ssambbong.gymjjak.dashboard.admin.application.usecase.AdminDashboardQueryUseCase;
import com.ssambbong.gymjjak.dashboard.admin.presentation.api.response.AdminContentStatisticsResponse;
import com.ssambbong.gymjjak.dashboard.admin.presentation.api.response.AdminDashboardResponseCode;
import com.ssambbong.gymjjak.dashboard.admin.presentation.api.response.AdminMemberStatisticsResponse;
import com.ssambbong.gymjjak.dashboard.admin.presentation.api.response.AdminRevenueStatisticsResponse;
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
            description = "전체 이용자 수, 전체 트레이너 수, 전체 헬스장 수, 최근 6개월 월별 가입자 수 반환 "
    )
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

    @GetMapping("/contents")
    @Operation(
            summary = "관리자 대시보드 콘텐츠 현황 조회",
            description = "활성 PT 수, 블라인드 PT 수, 처리 대기 신고 그룹 수를 반환합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 대시보드 콘텐츠 현황 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse<AdminContentStatisticsResponse>> findContentStatistics() {
        AdminContentStatisticsResult result =
                adminDashboardQueryUseCase.findContentStatistics();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        AdminDashboardResponseCode.ADMIN_CONTENT_STATISTICS_FOUND,
                        AdminContentStatisticsResponse.from(result)
                )
        );
    }

    @GetMapping("/revenues")
    @Operation(
            summary = "관리자 대시보드 월별 매출 통계",
            description = "이번 달을 포함한 최근 6개월의 월별 PT 수수료, 구독권 매출, 총매출을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 대시보드 매출 통계 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse<AdminRevenueStatisticsResponse>>
    findRevenueStatistics() {

        AdminRevenueStatisticsResult result = adminDashboardQueryUseCase.findRevenueStatistics();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        AdminDashboardResponseCode.ADMIN_REVENUE_STATISTICS_FOUND,
                        AdminRevenueStatisticsResponse.from(result)
                )
        );
    }
}
