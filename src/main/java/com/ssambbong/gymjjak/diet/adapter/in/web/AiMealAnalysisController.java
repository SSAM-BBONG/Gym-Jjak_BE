package com.ssambbong.gymjjak.diet.adapter.in.web;

import com.ssambbong.gymjjak.diet.adapter.in.web.request.AiMealAnalysisRequest;
import com.ssambbong.gymjjak.diet.adapter.in.web.response.AiMealAnalysisResponse;
import com.ssambbong.gymjjak.diet.adapter.in.web.response.MealAnalysisResponseCode;
import com.ssambbong.gymjjak.diet.application.command.AiMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.AiMealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.result.AiMealAnalysisResult;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diet/meals")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "식단 AI 분석", description = "음식 이미지를 AI로 분석해 식단을 저장하는 API")
public class AiMealAnalysisController {
    private final AiMealAnalysisUseCase aiMealAnalysisUseCase;
    private final MealTypeMapper mealTypeMapper;

    @PostMapping("/ai-analyze")
    @Operation(summary = "AI 식단 분석 및 저장", description = "활성 AI 구독 사용자의 음식 이미지를 분석하고 결과를 식단으로 저장합니다.")
    public ResponseEntity<GlobalApiResponse<AiMealAnalysisResponse>> analyze(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody AiMealAnalysisRequest request) {
        // Presigned URL로 S3 업로드를 마친 파일 메타데이터를 애플리케이션 계층 명령으로 변환한다.
        AiMealAnalysisResult result = aiMealAnalysisUseCase.analyze(new AiMealAnalysisCommand(
                authUser.userId(), request.file().fileKey(), request.file().originalName(),
                request.file().contentType(), request.file().fileSize(),
                mealTypeMapper.toEnum(request.mealType()), request.mealTime()));
        return ResponseEntity.status(201).body(GlobalApiResponse.created(
                MealAnalysisResponseCode.AI_MEAL_ANALYZED, toResponse(result)));
    }

    private AiMealAnalysisResponse toResponse(AiMealAnalysisResult result) {
        return new AiMealAnalysisResponse(
                result.mealId(), mealTypeMapper.toKorean(result.mealType()), result.mealTime(), result.menu(),
                result.fileId(), result.kcal(), result.carbohydrate(), result.protein(), result.fat(),
                result.evaluation(), result.confidence(), result.warnings(), result.createdAt(), result.updatedAt());
    }
}
