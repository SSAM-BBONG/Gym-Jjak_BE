package com.ssambbong.gymjjak.diet.adapter.in.web;

import com.ssambbong.gymjjak.diet.adapter.in.web.request.NutritionGoalRequest;
import com.ssambbong.gymjjak.diet.adapter.in.web.request.UpdateNutritionGoalRequest;
import com.ssambbong.gymjjak.diet.adapter.in.web.response.NutritionGoalResponse;
import com.ssambbong.gymjjak.diet.adapter.in.web.response.NutritionGoalResponseCode;
import com.ssambbong.gymjjak.diet.application.command.NutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateNutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.port.in.NutritionGoalUseCase;
import com.ssambbong.gymjjak.diet.application.result.NutritionGoalResult;
import com.ssambbong.gymjjak.diet.domain.exception.InvalidNutritionGoalUpdateException;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diet/nutrition-goals")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "영양 목표 관리", description = "로그인 사용자의 일일 영양 목표 CRUD API")
public class NutritionGoalController {
    private final NutritionGoalUseCase nutritionGoalUseCase;

    @PostMapping
    @Operation(summary = "영양 목표 등록", description = "로그인 사용자의 일일 단백질, 탄수화물, 지방, 칼로리 목표를 등록합니다. 사용자당 한 건만 등록할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "영양 목표 등록 성공",
                    content = @Content(schema = @Schema(implementation = NutritionGoalResponse.class))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 0보다 작은 목표 값", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 영양 목표가 등록되어 있음", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<NutritionGoalResponse>> create(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody NutritionGoalRequest request) {
        NutritionGoalResult result = nutritionGoalUseCase.create(new NutritionGoalCommand(authUser.userId(),
                request.goalProtein(), request.goalCarbohydrate(), request.goalFat(), request.dailyGoalKcal()));
        return ResponseEntity.status(201).body(GlobalApiResponse.created(
                NutritionGoalResponseCode.GOAL_CREATED, toResponse(result)));
    }

    @GetMapping
    @Operation(summary = "영양 목표 조회", description = "로그인 사용자의 일일 영양 목표를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "영양 목표 조회 성공",
                    content = @Content(schema = @Schema(implementation = NutritionGoalResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "등록된 영양 목표가 없음", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<NutritionGoalResponse>> get(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(GlobalApiResponse.ok(NutritionGoalResponseCode.GOAL_FETCHED,
                toResponse(nutritionGoalUseCase.get(authUser.userId()))));
    }

    @PatchMapping
    @Operation(summary = "영양 목표 부분 수정", description = "로그인 사용자의 영양 목표 중 요청에 포함된 값만 수정합니다. 한 개 이상의 필드를 입력해야 하며 null은 허용하지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "영양 목표 수정 성공",
                    content = @Content(schema = @Schema(implementation = NutritionGoalResponse.class))),
            @ApiResponse(responseCode = "400", description = "수정 필드가 없거나 null 또는 0보다 작은 목표 값", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "등록된 영양 목표가 없음", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<NutritionGoalResponse>> update(
            @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UpdateNutritionGoalRequest request) {
        if (!request.hasAnyField()
                || (request.isProteinPresent() && request.getGoalProtein() == null)
                || (request.isCarbohydratePresent() && request.getGoalCarbohydrate() == null)
                || (request.isFatPresent() && request.getGoalFat() == null)
                || (request.isKcalPresent() && request.getDailyGoalKcal() == null)) {
            throw new InvalidNutritionGoalUpdateException();
        }
        UpdateNutritionGoalCommand command = new UpdateNutritionGoalCommand(authUser.userId(),
                request.getGoalProtein(), request.isProteinPresent(),
                request.getGoalCarbohydrate(), request.isCarbohydratePresent(),
                request.getGoalFat(), request.isFatPresent(),
                request.getDailyGoalKcal(), request.isKcalPresent());
        return ResponseEntity.ok(GlobalApiResponse.ok(NutritionGoalResponseCode.GOAL_UPDATED,
                toResponse(nutritionGoalUseCase.update(command))));
    }

    @DeleteMapping
    @Operation(summary = "영양 목표 삭제", description = "로그인 사용자의 일일 영양 목표를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "영양 목표 삭제 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "등록된 영양 목표가 없음", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<Void>> delete(@AuthenticationPrincipal AuthUser authUser) {
        nutritionGoalUseCase.delete(authUser.userId());
        return ResponseEntity.ok(GlobalApiResponse.ok(NutritionGoalResponseCode.GOAL_DELETED));
    }

    private NutritionGoalResponse toResponse(NutritionGoalResult result) {
        return new NutritionGoalResponse(result.goalId(), result.goalProtein(), result.goalCarbohydrate(),
                result.goalFat(), result.dailyGoalKcal(), result.createdAt(), result.updatedAt());
    }
}
