package com.ssambbong.gymjjak.diet.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NutritionGoalErrorCode implements ErrorCode {
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "NUTRITION_GOAL_404_1", "영양 목표를 찾을 수 없습니다."),
    GOAL_ALREADY_EXISTS(HttpStatus.CONFLICT, "NUTRITION_GOAL_409_1", "이미 등록된 영양 목표가 있습니다."),
    INVALID_UPDATE_REQUEST(HttpStatus.BAD_REQUEST, "NUTRITION_GOAL_400_1", "수정할 영양 목표를 한 개 이상 올바르게 입력해야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
