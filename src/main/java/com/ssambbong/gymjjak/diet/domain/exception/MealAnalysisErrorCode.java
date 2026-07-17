package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MealAnalysisErrorCode implements ErrorCode {
    MEAL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEAL_404_1", "식단 기록을 찾을 수 없습니다."),
    INVALID_MEAL_TYPE(HttpStatus.BAD_REQUEST, "MEAL_400_1", "식사 유형은 아침, 점심, 저녁, 간식 중 하나여야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
