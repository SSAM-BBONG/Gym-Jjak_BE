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
    AI_NUTRITION_ACCESS_REQUIRED(HttpStatus.FORBIDDEN, "MEAL_403_1", "영양성분을 저장하려면 활성 AI 구독이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
