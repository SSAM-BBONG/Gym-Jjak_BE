package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MealAnalysisResponseCode implements ResponseCode {
    MEAL_CREATED("MEAL_201_1", "식단을 등록했습니다."),
    MEAL_FETCHED("MEAL_200_1", "식단을 조회했습니다."),
    MEAL_LIST_FETCHED("MEAL_200_2", "식단 목록을 조회했습니다."),
    MEAL_UPDATED("MEAL_200_3", "식단을 수정했습니다."),
    MEAL_DELETED("MEAL_200_4", "식단을 삭제했습니다.");

    private final String code;
    private final String message;
}
