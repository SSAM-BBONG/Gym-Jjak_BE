package com.ssambbong.gymjjak.exercise.adapter.in.web;

import com.ssambbong.gymjjak.exercise.adapter.in.web.request.CreateExerciseRequest;
import com.ssambbong.gymjjak.exercise.adapter.in.web.request.UpdateExerciseRequest;
import com.ssambbong.gymjjak.exercise.adapter.in.web.response.CreateExerciseResponse;
import com.ssambbong.gymjjak.exercise.adapter.in.web.response.ExerciseResponse;
import com.ssambbong.gymjjak.exercise.adapter.in.web.response.ExerciseResponseCode;
import com.ssambbong.gymjjak.exercise.application.port.in.ExerciseUseCase;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.PartTypeNameMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercises")
public class AdminExerciseController {

    private final ExerciseUseCase exerciseUseCase;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "운동 종목 등록", description = "관리자가 부위별 운동 종목을 등록합니다.")
    public ResponseEntity<GlobalApiResponse<CreateExerciseResponse>> createExercise(
            @Valid @RequestBody CreateExerciseRequest request
    ) {
        Long exerciseId = exerciseUseCase.createExercise(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.created(
                        ExerciseResponseCode.EXERCISE_CREATED,
                        CreateExerciseResponse.from(exerciseId)
                ));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{exerciseId}")
    @Operation(summary = "운동 종목 수정", description = "관리자가 운동 종목의 이름을 수정합니다.")
    public ResponseEntity<GlobalApiResponse<Void>> updateExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody UpdateExerciseRequest request
    ) {
        exerciseUseCase.updateExercise(exerciseId, request.toCommand());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(ExerciseResponseCode.EXERCISE_UPDATED)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{exerciseId}")
    @Operation(summary = "운동 종목 삭제", description = "관리자가 운동 종목을 삭제합니다.")
    public ResponseEntity<GlobalApiResponse<Void>> deleteExercise(
            @PathVariable Long exerciseId
    ) {
        exerciseUseCase.deleteExercise(exerciseId);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(ExerciseResponseCode.EXERCISE_DELETED)
        );
    }

    @Validated
    @GetMapping
    @Operation(summary = "부위별 운동 종목 조회", description = "선택한 부위에 등록된 운동 종목을 검색합니다.")
    public ResponseEntity<GlobalApiResponse<List<ExerciseResponse>>> findExercises(
            @RequestParam @NotBlank(message = "운동 부위는 필수입니다.") String part,
            @RequestParam(required = false) String keyword
    ) {
        List<ExerciseResponse> response = exerciseUseCase
                .findExercises(PartTypeNameMapper.fromKoreanName(part), keyword)
                .stream()
                .map(ExerciseResponse::from)
                .toList();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        ExerciseResponseCode.EXERCISES_FETCHED,
                        response
                )
        );
    }
}
