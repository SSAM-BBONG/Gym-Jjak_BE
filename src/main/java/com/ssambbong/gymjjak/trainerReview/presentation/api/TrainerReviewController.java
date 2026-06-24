package com.ssambbong.gymjjak.trainerReview.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainerReview.application.usecase.TrainerReviewCommandUseCase;
import com.ssambbong.gymjjak.trainerReview.presentation.api.mapper.TrainerReviewMapper;
import com.ssambbong.gymjjak.trainerReview.presentation.api.request.CreateTrainerReviewRequest;
import com.ssambbong.gymjjak.trainerReview.presentation.api.response.CreateTrainerReviewResponse;
import com.ssambbong.gymjjak.trainerReview.presentation.api.response.TrainerReviewResponseCode;
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
@RequestMapping("/api/pt-courses/{ptCourseId}/reviews")
@RequiredArgsConstructor
public class TrainerReviewController {

    private final TrainerReviewCommandUseCase trainerReviewCommandUseCase;
    private final TrainerReviewMapper trainerReviewMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "강사평 작성", description = "완료된 PT 예약에 대해 강사평을 작성한다.")
    public ResponseEntity<GlobalApiResponse<CreateTrainerReviewResponse>> createReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptCourseId,
            @RequestBody @Valid CreateTrainerReviewRequest request
    ) {
        Long reviewId = trainerReviewCommandUseCase.createReview(
                trainerReviewMapper.toCommand(request, authUser.userId(), ptCourseId));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GlobalApiResponse.created(TrainerReviewResponseCode.TRAINER_REVIEW_CREATED,
                        CreateTrainerReviewResponse.from(reviewId))
        );
    }
}
