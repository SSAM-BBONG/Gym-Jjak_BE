package com.ssambbong.gymjjak.pt.trainerReview.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.trainerReview.application.command.DeleteTrainerReviewCommand;
import com.ssambbong.gymjjak.pt.trainerReview.application.query.TrainerReviewListQuery;
import com.ssambbong.gymjjak.pt.trainerReview.application.query.TrainerReviewSortType;
import com.ssambbong.gymjjak.pt.trainerReview.application.usecase.TrainerReviewCommandUseCase;
import com.ssambbong.gymjjak.pt.trainerReview.application.usecase.TrainerReviewQueryUseCase;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.mapper.TrainerReviewMapper;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.request.CreateTrainerReviewRequest;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.request.UpdateTrainerReviewRequest;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.response.CreateTrainerReviewResponse;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.response.TrainerReviewListResponse;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.response.TrainerReviewResponseCode;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.response.TrainerReviewSummaryResponse;
import com.ssambbong.gymjjak.pt.trainerReview.presentation.api.response.UpdateTrainerReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "강사평", description = "강사평 관련 API")
@RestController
@RequiredArgsConstructor
public class TrainerReviewController {

    private final TrainerReviewCommandUseCase trainerReviewCommandUseCase;
    private final TrainerReviewQueryUseCase trainerReviewQueryUseCase;
    private final TrainerReviewMapper trainerReviewMapper;

    @PostMapping("/api/pt-courses/{ptCourseId}/reservations/{ptReservationId}/reviews")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "강사평 작성", description = "완료된 PT 예약에 대해 강사평을 작성한다.")
    public ResponseEntity<GlobalApiResponse<CreateTrainerReviewResponse>> createReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptCourseId,
            @PathVariable Long ptReservationId,
            @RequestBody @Valid CreateTrainerReviewRequest request
    ) {
        Long reviewId = trainerReviewCommandUseCase.createReview(
                trainerReviewMapper.toCommand(request, authUser.userId(), ptCourseId, ptReservationId));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GlobalApiResponse.created(TrainerReviewResponseCode.TRAINER_REVIEW_CREATED,
                        CreateTrainerReviewResponse.from(reviewId))
        );
    }

    @PatchMapping("/api/reviews/{reviewId}")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "강사평 수정", description = "사용자가 본인 강사평을 수정한다.")
    public ResponseEntity<GlobalApiResponse<UpdateTrainerReviewResponse>> updateReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateTrainerReviewRequest request
    ) {
        Long updatedId = trainerReviewCommandUseCase.updateReview(
                trainerReviewMapper.toCommand(request, authUser.userId(), reviewId));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(TrainerReviewResponseCode.TRAINER_REVIEW_UPDATED,
                        UpdateTrainerReviewResponse.from(updatedId)));
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "강사평 삭제", description = "사용자가 본인 강사평을 삭제한다.")
    public ResponseEntity<GlobalApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long reviewId
    ) {
        trainerReviewCommandUseCase.deleteReview(new DeleteTrainerReviewCommand(authUser.userId(), reviewId));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(TrainerReviewResponseCode.TRAINER_REVIEW_DELETED, null));
    }

    @GetMapping("/api/trainer-profiles/{trainerProfileId}/reviews/summary")
    @Operation(summary = "강사평 요약 조회", description = "트레이너 정보 + 평균 별점 + 별점 분포를 조회한다.")
    public ResponseEntity<GlobalApiResponse<TrainerReviewSummaryResponse>> getReviewSummary(
            @PathVariable Long trainerProfileId
    ) {
        return ResponseEntity.ok(
                GlobalApiResponse.ok(TrainerReviewResponseCode.TRAINER_REVIEW_FETCHED,
                        TrainerReviewSummaryResponse.from(trainerReviewQueryUseCase.getSummary(trainerProfileId))));
    }

    @GetMapping("/api/trainer-profiles/{trainerProfileId}/reviews")
    @Operation(summary = "강사평 목록 조회", description = "트레이너의 강사평 목록을 커서 기반으로 조회한다.")
    public ResponseEntity<GlobalApiResponse<TrainerReviewListResponse>> getReviews(
            @PathVariable Long trainerProfileId,
            @Parameter(description = "커서 ID (이전 응답의 nextCursor, 첫 페이지는 미입력)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "커서 별점 (HIGH_RATING 정렬 시 이전 응답의 nextCursorRating, 첫 페이지는 미입력)") @RequestParam(required = false) Integer cursorRating,
            @Parameter(description = "페이지 크기 (기본값: 10, 최대: 50)") @Max(50) @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준", schema = @Schema(type = "string", allowableValues = {"LATEST", "HIGH_RATING"})) @RequestParam(defaultValue = "LATEST") TrainerReviewSortType sort
    ) {
        return ResponseEntity.ok(
                GlobalApiResponse.ok(TrainerReviewResponseCode.TRAINER_REVIEW_FETCHED,
                        TrainerReviewListResponse.from(
                                trainerReviewQueryUseCase.getReviews(
                                        new TrainerReviewListQuery(trainerProfileId, cursor, cursorRating, size, sort)))));
    }
}
