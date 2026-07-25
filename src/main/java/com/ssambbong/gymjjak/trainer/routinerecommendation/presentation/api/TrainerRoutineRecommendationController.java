package com.ssambbong.gymjjak.trainer.routinerecommendation.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.in.TrainerRoutineRecommendationUseCase;
import com.ssambbong.gymjjak.trainer.routinerecommendation.presentation.api.request.RecommendTrainerRoutineRequest;
import com.ssambbong.gymjjak.trainer.routinerecommendation.presentation.api.response.TrainerRoutineRecommendationResponse;
import com.ssambbong.gymjjak.trainer.routinerecommendation.presentation.api.response.TrainerRoutineRecommendationResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers/members")
@RequiredArgsConstructor
public class TrainerRoutineRecommendationController {
    private final TrainerRoutineRecommendationUseCase trainerRoutineRecommendationUseCase;

    @PostMapping("/{memberId}/routine-recommendations")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<GlobalApiResponse<TrainerRoutineRecommendationResponse>> recommend(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long memberId,
            @Valid @RequestBody RecommendTrainerRoutineRequest request
    ) {
        var result = trainerRoutineRecommendationUseCase.recommend(new RecommendTrainerRoutineCommand(
                authUser.userId(), memberId, request.gender(), request.age(), request.heightCm(), request.weightKg(), request.goal()));
        return ResponseEntity.ok(GlobalApiResponse.ok(
                TrainerRoutineRecommendationResponseCode.TRAINER_ROUTINE_RECOMMENDATION_CREATED,
                TrainerRoutineRecommendationResponse.from(result)));
    }
}
