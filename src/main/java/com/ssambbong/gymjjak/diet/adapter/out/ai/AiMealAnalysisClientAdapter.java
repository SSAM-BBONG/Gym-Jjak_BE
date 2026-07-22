package com.ssambbong.gymjjak.diet.adapter.out.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssambbong.gymjjak.diet.application.port.out.MealNutritionAnalysisPort;
import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;
import com.ssambbong.gymjjak.diet.domain.exception.AiMealAnalysisException;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class AiMealAnalysisClientAdapter implements MealNutritionAnalysisPort {
    private final RestClient restClient;
    private final AiMealAnalysisProperties properties;

    public AiMealAnalysisClientAdapter(
            @Qualifier("aiMealAnalysisRestClient") RestClient restClient,
            AiMealAnalysisProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public AnalysisResult analyze(AnalysisRequest request) {
        try {
            AiResponse response = restClient.post()
                    .uri(properties.getAnalyzePath())
                    .body(AiRequest.from(request))
                    .retrieve()
                    // FastAPI가 음식 미검출을 422로 반환하면 사용자에게 구분 가능한 비즈니스 오류로 변환한다.
                    .onStatus(status -> status.value() == 422, (httpRequest, httpResponse) -> {
                        String responseBody = new String(httpResponse.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        log.warn("AI 식단 분석 요청이 거절되었습니다. status=422, body={}", responseBody);
                        if (responseBody.contains("FOOD_NOT_DETECTED")) {
                            throw new AiMealAnalysisException(MealAnalysisErrorCode.FOOD_NOT_DETECTED);
                        }
                        throw new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_SERVER_ERROR);
                    })
                    // 내부 인증 실패와 AI 서버의 그 외 오류는 사용자 인증 오류로 노출하지 않고 서버 간 장애로 처리한다.
                    .onStatus(HttpStatusCode::isError, (httpRequest, httpResponse) -> {
                        log.warn("AI 식단 분석 서버가 오류를 반환했습니다. status={}", httpResponse.getStatusCode());
                        throw new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_SERVER_ERROR);
                    })
                    .body(AiResponse.class);

            if (response == null) {
                throw new AiMealAnalysisException(MealAnalysisErrorCode.INVALID_AI_ANALYSIS_RESULT);
            }
            return response.toResult();
        } catch (AiMealAnalysisException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.warn("AI 식단 분석 서버 통신에 실패했습니다. baseUrl={}, path={}, exception={}",
                    properties.getBaseUrl(), properties.getAnalyzePath(), exception.toString(), exception);
            throw convertException(exception);
        }
    }

    private AiMealAnalysisException convertException(RestClientException exception) {
        // 동기 HTTP 호출의 연결 또는 응답 제한 시간 초과는 504 오류로 변환한다.
        if (exception instanceof ResourceAccessException && hasTimeoutCause(exception)) {
            return new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_TIMEOUT, exception);
        }
        // DNS, 연결 거부, 응답 역직렬화 실패 등 나머지 RestClient 오류는 AI 서버 장애로 처리한다.
        return new AiMealAnalysisException(MealAnalysisErrorCode.AI_ANALYSIS_SERVER_ERROR, exception);
    }

    private boolean hasTimeoutCause(Throwable exception) {
        Throwable cause = exception;
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
