package com.ssambbong.gymjjak.trainerReview.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainerReview.application.usecase.TrainerReviewCommandUseCase;
import com.ssambbong.gymjjak.trainerReview.presentation.api.mapper.TrainerReviewMapper;
import com.ssambbong.gymjjak.trainerReview.presentation.api.request.CreateTrainerReviewRequest;
import com.ssambbong.gymjjak.trainerReview.presentation.api.request.UpdateTrainerReviewRequest;
import com.ssambbong.gymjjak.trainerReview.presentation.api.response.CreateTrainerReviewResponse;
import com.ssambbong.gymjjak.trainerReview.presentation.api.response.TrainerReviewResponseCode;
import com.ssambbong.gymjjak.trainerReview.presentation.api.response.UpdateTrainerReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
}
