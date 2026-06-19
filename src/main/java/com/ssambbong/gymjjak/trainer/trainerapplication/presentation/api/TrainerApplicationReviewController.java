package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.ApproveTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.RejectTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.FindTrainerApplicationsCondition;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationListResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationReviewQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request.FindTrainerApplicationsRequest;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request.RejectTrainerApplicationRequest;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/trainer-applications")
@RequiredArgsConstructor
public class TrainerApplicationReviewController {

    private final TrainerApplicationReviewQueryUseCase trainerApplicationReviewQueryUseCase;
    private final TrainerApplicationCommandUseCase trainerApplicationCommandUseCase;

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

    @GetMapping("/{trainerApplicationId}")
    @Operation(
            summary = "트레이너 신청서 관리자 상세 조회",
            description = "관리자가 특정 트레이너 신청서를 상세 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "트레이너 신청서 관리자 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "트레이너 신청서를 찾을 수 없음")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse<TrainerApplicationReviewDetailResponse>> getTrainerApplicationReviewDetail(
            @PathVariable long trainerApplicationId
            ) {

        TrainerApplicationReviewDetailResult result =
                trainerApplicationReviewQueryUseCase.getTrainerApplicationReviewDetail(trainerApplicationId);

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_REVIEW_DETAIL_FOUND,
                        TrainerApplicationReviewDetailResponse.from(result)
                )
        );
    }

    @PatchMapping("/{trainerApplicationId}/approve")
    @Operation(
            summary = "트레이너 신청 승인",
            description = "관리자가 트레이너 신청을 승인합니다. 승인 시 사용자 권한이 TRAINER로 변경되고 트레이너 프로필이 생성됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "트레이너 신청 승인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "트레이너 신청서를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "PENDING 상태가 아니어서 승인할 수 없음")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalApiResponse<ApproveTrainerApplicationResponse>> approveTrainerApplication(
            @PathVariable @Positive long trainerApplicationId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Long trainerProfileId = trainerApplicationCommandUseCase.approveTrainerApplication(
                new ApproveTrainerApplicationCommand(
                        trainerApplicationId,
                        authUser.userId()
                )
        );

        return ResponseEntity.status(201).body(
                GlobalApiResponse.created(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_APPROVED,
                        new ApproveTrainerApplicationResponse(
                                trainerApplicationId,
                                trainerProfileId
                        )
                )
        );
    }

    @PatchMapping("/{trainerApplicationId}/reject")
    @Operation(
            summary = "트레이너 신청 반려",
            description = "관리자가 PENDING 상태의 트레이너 신청을 반려합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "트레이너 신청 반려 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "반려 사유 누락 또는 잘못된 요청"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "트레이너 신청서를 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "PENDING 상태가 아니어서 반려할 수 없음"
            )
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<
            GlobalApiResponse<Void>
            > rejectTrainerApplication(
            @PathVariable @Positive long trainerApplicationId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid RejectTrainerApplicationRequest request
    ) {
        trainerApplicationCommandUseCase.rejectTrainerApplication(
                new RejectTrainerApplicationCommand(
                        trainerApplicationId,
                        authUser.userId(),
                        request.rejectReason()
                )
        );

        return ResponseEntity.status(201).body(
                GlobalApiResponse.created(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_REJECTED,
                        null
                )
        );
    }
}
