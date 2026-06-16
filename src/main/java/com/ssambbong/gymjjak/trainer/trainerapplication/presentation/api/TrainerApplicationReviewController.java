package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationReviewQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request.FindTrainerApplicationsRequest;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.TrainerApplicationListResponse;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.TrainerApplicationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainer-applications")
@RequiredArgsConstructor
public class TrainerApplicationReviewController {

    private final TrainerApplicationReviewQueryUseCase trainerApplicationReviewQueryUseCase;

    @GetMapping
    @Operation(
            summary = "트레이너 신청 목록 조회",
            description = "트레이너 신청 목록을 상태와 검색어 기준으로 조회합니다. 기본 상태는 PENDING입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "트레이너 신청 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse<TrainerApplicationListResponse>> findTrainerApplications(
            @ModelAttribute @Valid FindTrainerApplicationsRequest request
    ) {
        TrainerApplicationListResult result =
                trainerApplicationReviewQueryUseCase.findTrainerApplications(
                        new FindTrainerApplicationsCondition(
                                request.resolvedStatus(),
                                request.normalizedKeyword(),
                                request.resolvedPage(),
                                request.resolvedSize()
                        )
                );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_LIST_FOUND,
                        TrainerApplicationListResponse.from(result)
                )
        );
    }
}
