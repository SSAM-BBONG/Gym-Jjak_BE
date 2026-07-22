package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MealAnalysisErrorCode implements ErrorCode {
    MEAL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEAL_404_1", "식단 기록을 찾을 수 없습니다."),
    INVALID_MEAL_TYPE(HttpStatus.BAD_REQUEST, "MEAL_400_1", "식사 유형은 아침, 점심, 저녁, 간식 중 하나여야 합니다."),
    INVALID_UPDATE_REQUEST(HttpStatus.BAD_REQUEST, "MEAL_400_2", "수정할 식단 정보를 한 개 이상 올바르게 입력해야 합니다."),
    AI_NUTRITION_ACCESS_REQUIRED(HttpStatus.FORBIDDEN, "MEAL_403_1", "영양성분을 저장하려면 활성 AI 구독이 필요합니다."),
    MEAL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MEAL_403_2", "식단을 조회할 권한이 없습니다."),
    INVALID_AI_ANALYSIS_RESULT(HttpStatus.BAD_GATEWAY, "MEAL_AI_502_1", "AI 식단 분석 결과가 올바르지 않습니다."),
    AI_ANALYSIS_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "MEAL_AI_502_2", "AI 식단 분석 서버 호출에 실패했습니다."),
    AI_ANALYSIS_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "MEAL_AI_504_1", "AI 식단 분석 요청 시간이 초과되었습니다."),
    FOOD_NOT_DETECTED(HttpStatus.UNPROCESSABLE_ENTITY, "MEAL_AI_422_1", "이미지에서 분석 가능한 음식을 찾지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
