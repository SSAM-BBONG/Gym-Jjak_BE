package com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.usecase.PtRecommendationUseCase;
import com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.request.PtRecommendationRequest;
import com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.response.PtRecommendationResponse;
import com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.response.PtRecommendationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PT 추천", description = "AI 기반 PT 코스 추천 API")
@RestController
@RequestMapping("/api/pt-recommendations")
@RequiredArgsConstructor
public class PtRecommendationController {

    private final PtRecommendationUseCase ptRecommendationUseCase;

    // 온보딩 기준주소+2차온보딩(부위/거리)으로 1차 필터링 후, 온보딩/PT이력 프로필과 함께
    // FastAPI에 전달해 AI가 최종 PT코스를 추천한다. 구독 여부와 무관하게 전 회원 무료 제공.
    @Operation(summary = "PT 추천", description = "회원의 온보딩 정보와 2차 온보딩 응답(부위/거리/통증)을 종합해 AI가 PT코스를 추천한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (부위 미선택, 통증 정보 불일치 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "조건에 맞는 PT코스를 찾지 못함"),
            @ApiResponse(responseCode = "502", description = "AI 서버 호출 실패 또는 결과 오류"),
            @ApiResponse(responseCode = "504", description = "AI 서버 응답 시간 초과")
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<PtRecommendationResponse>> recommend(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid PtRecommendationRequest request
    ) {
        PtRecommendationResponse response = PtRecommendationResponse.from(
                ptRecommendationUseCase.recommend(request.toCommand(authUser.userId())));

        return ResponseEntity.ok(
                GlobalApiResponse.ok(PtRecommendationResponseCode.PT_RECOMMENDATION_FETCHED, response));
    }
}
