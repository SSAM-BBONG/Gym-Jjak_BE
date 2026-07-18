package com.ssambbong.gymjjak.diet.adapter.out.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssambbong.gymjjak.diet.application.port.out.MealNutritionAnalysisPort;
import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;
import com.ssambbong.gymjjak.diet.domain.exception.AiMealAnalysisException;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
public class AiMealAnalysisClientAdapter implements MealNutritionAnalysisPort {
    private final WebClient webClient;
    private final AiMealAnalysisProperties properties;

    public AiMealAnalysisClientAdapter(
            @Qualifier("aiMealAnalysisWebClient") WebClient webClient,
            AiMealAnalysisProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    @Override
    public Mono<AnalysisResult> analyze(AnalysisRequest request) {
        return webClient.post()
                .uri(properties.getAnalyzePath())
                .bodyValue(AiRequest.from(request))
                .retrieve()
                // FastAPI가 음식 미검출을 422로 반환하면 사용자에게 구분 가능한 비즈니스 오류로 변환한다.
                .onStatus(status -> status.value() == 422, response ->
                        Mono.error(new AiMealAnalysisException(MealAnalysisErrorCode.FOOD_NOT_DETECTED)))
                // 내부 인증 실패와 AI 서버의 그 외 오류는 사용자 인증 오류로 노출하지 않고 서버 간 장애로 처리한다.
                .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_SERVER_ERROR)))
                .bodyToMono(AiResponse.class)
                .switchIfEmpty(Mono.error(
                        new AiMealAnalysisException(MealAnalysisErrorCode.INVALID_AI_ANALYSIS_RESULT)))
                .map(AiResponse::toResult)
                .onErrorMap(this::convertException);
    }

    private Throwable convertException(Throwable exception) {
        if (exception instanceof AiMealAnalysisException) {
            return exception;
        }
        // Reactor Netty의 응답 제한 시간 또는 Mono timeout에서 발생한 오류는 504로 변환한다.
        if (exception instanceof TimeoutException || hasTimeoutCause(exception)) {
            return new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_TIMEOUT, exception);
        }
        // DNS, 연결 거부 등 요청 자체를 전송하지 못한 경우는 AI 서버 연결 장애로 처리한다.
        if (exception instanceof WebClientRequestException
                || exception instanceof WebClientResponseException) {
            return new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_SERVER_ERROR, exception);
        }
        return new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_SERVER_ERROR, exception);
    }

    private boolean hasTimeoutCause(Throwable exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            String name = cause.getClass().getSimpleName().toLowerCase();
            if (name.contains("timeout")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private record AiRequest(
            @JsonProperty("image_url") String imageUrl,
            @JsonProperty("meal_type") String mealType,
            @JsonProperty("meal_time") LocalDateTime mealTime,
            @JsonProperty("nutrition_goal") GoalRequest nutritionGoal,
            @JsonProperty("today_intake") IntakeRequest todayIntake
    ) {
        static AiRequest from(AnalysisRequest request) {
            return new AiRequest(request.imageUrl(), request.mealType(), request.mealTime(),
                    GoalRequest.from(request.nutritionGoal()), IntakeRequest.from(request.todayIntake()));
        }
    }

    private record GoalRequest(Long protein, Long carbohydrate, Long fat, Long kcal) {
        static GoalRequest from(NutritionGoalSnapshot goal) {
            return goal == null ? null : new GoalRequest(goal.protein(), goal.carbohydrate(), goal.fat(), goal.kcal());
        }
    }

    private record IntakeRequest(Long kcal, BigDecimal carbohydrate, BigDecimal protein, BigDecimal fat) {
        static IntakeRequest from(MealNutritionSummary summary) {
            return new IntakeRequest(summary.kcal(), summary.carbohydrate(), summary.protein(), summary.fat());
        }
    }

    private record AiResponse(
            String menu,
            Long kcal,
            BigDecimal carbohydrate,
            BigDecimal protein,
            BigDecimal fat,
            String evaluation,
            BigDecimal confidence,
            List<String> warnings
    ) {
        AnalysisResult toResult() {
            return new AnalysisResult(menu, kcal, carbohydrate, protein, fat, evaluation, confidence, warnings);
        }
    }
}
