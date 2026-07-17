package com.ssambbong.gymjjak.diet.adapter.in.web;

import com.ssambbong.gymjjak.diet.adapter.in.web.request.MealAnalysisRequest;
import com.ssambbong.gymjjak.diet.adapter.in.web.request.UpdateMealAnalysisRequest;
import com.ssambbong.gymjjak.diet.adapter.in.web.response.*;
import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.MealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;
import com.ssambbong.gymjjak.diet.domain.exception.InvalidMealUpdateException;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/diet/meals")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "식단 관리", description = "사용자 식단 등록·조회·수정·삭제 API")
public class MealAnalysisController {

    private static final int MAX_PAGE_SIZE = 100;

    private final MealAnalysisUseCase mealAnalysisUseCase;
    private final MealTypeMapper mealTypeMapper;

    @PostMapping
    @Operation(summary = "식단 등록", description = "로그인한 사용자의 식단을 등록합니다. 식사 유형은 한글로 입력합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "식단 등록 성공",
                    content = @Content(schema = @Schema(implementation = MealAnalysisResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<MealAnalysisResponse>> create(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody MealAnalysisRequest request) {
        MealAnalysisResult result = mealAnalysisUseCase.create(toCommand(authUser.userId(), request));
        return ResponseEntity.status(201).body(GlobalApiResponse.created(
                MealAnalysisResponseCode.MEAL_CREATED, toResponse(result)));
    }

    @GetMapping("/{mealId}")
    @Operation(summary = "식단 단건 조회", description = "로그인한 사용자가 본인 소유의 식단 한 건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "식단 조회 성공",
                    content = @Content(schema = @Schema(implementation = MealAnalysisResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "식단을 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<MealAnalysisResponse>> get(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "조회할 식단 ID", example = "1", required = true)
            @PathVariable Long mealId) {
        return ResponseEntity.ok(GlobalApiResponse.ok(MealAnalysisResponseCode.MEAL_FETCHED,
                toResponse(mealAnalysisUseCase.get(authUser.userId(), mealId))));
    }

    @GetMapping
    @Operation(summary = "식단 목록 조회", description = "로그인한 사용자의 식단을 식사 일시 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "식단 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = MealAnalysisPageResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<MealAnalysisPageResponse>> getList(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 조회 개수(최대 100)", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        MealPageQuery query = new MealPageQuery(authUser.userId(), Math.max(page, 0), safeSize);
        MealPageResult<MealAnalysisResponse> results = mealAnalysisUseCase.getList(query)
                .map(this::toResponse);
        return ResponseEntity.ok(GlobalApiResponse.ok(MealAnalysisResponseCode.MEAL_LIST_FETCHED,
                MealAnalysisPageResponse.from(results)));
    }

    @PatchMapping("/{mealId}")
    @Operation(summary = "식단 부분 수정", description = "로그인한 사용자가 본인 소유 식단에서 요청에 포함한 필드만 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "식단 수정 성공",
                    content = @Content(schema = @Schema(implementation = MealAnalysisResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "식단을 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<MealAnalysisResponse>> update(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "수정할 식단 ID", example = "1", required = true)
            @PathVariable Long mealId,
            @Valid @RequestBody UpdateMealAnalysisRequest request) {
        MealAnalysisResult result = mealAnalysisUseCase.update(
                mealId,
                toUpdateCommand(authUser.userId(), request)
        );
        return ResponseEntity.ok(GlobalApiResponse.ok(MealAnalysisResponseCode.MEAL_UPDATED, toResponse(result)));
    }

    @DeleteMapping("/{mealId}")
    @Operation(summary = "식단 삭제", description = "로그인한 사용자가 본인 소유의 식단을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "식단 삭제 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "식단을 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<GlobalApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "삭제할 식단 ID", example = "1", required = true)
            @PathVariable Long mealId) {
        mealAnalysisUseCase.delete(authUser.userId(), mealId);
        return ResponseEntity.ok(GlobalApiResponse.ok(MealAnalysisResponseCode.MEAL_DELETED));
    }

    private MealAnalysisCommand toCommand(Long userId, MealAnalysisRequest request) {
        return new MealAnalysisCommand(userId, mealTypeMapper.toEnum(request.mealType()), request.mealTime(),
                request.menu().trim(), request.kcal(), request.fileId());
    }

    private UpdateMealAnalysisCommand toUpdateCommand(Long userId, UpdateMealAnalysisRequest request) {
        if (!request.hasAnyField()
                || (request.isMealTypePresent() && request.getMealType() == null)
                || (request.isMealTimePresent() && request.getMealTime() == null)
                || (request.isMenuPresent() && (request.getMenu() == null || request.getMenu().isBlank()))) {
            throw new InvalidMealUpdateException();
        }

        MealType mealType = request.isMealTypePresent()
                ? mealTypeMapper.toEnum(request.getMealType())
                : null;
        String menu = request.isMenuPresent() ? request.getMenu().trim() : null;

        return new UpdateMealAnalysisCommand(
                userId,
                mealType,
                request.isMealTypePresent(),
                request.getMealTime(),
                request.isMealTimePresent(),
                menu,
                request.isMenuPresent(),
                request.getKcal(),
                request.isKcalPresent(),
                request.getFileId(),
                request.isFileIdPresent()
        );
    }

    private MealAnalysisResponse toResponse(MealAnalysisResult result) {
        return new MealAnalysisResponse(result.mealId(), mealTypeMapper.toKorean(result.mealType()),
                result.mealTime(), result.menu(), result.kcal(), result.fileId(),
                result.createdAt(), result.updatedAt());
    }
}
