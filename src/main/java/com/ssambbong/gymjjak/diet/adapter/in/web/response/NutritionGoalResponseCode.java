package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public enum NutritionGoalResponseCode implements ResponseCode {
    GOAL_CREATED("NUTRITION_GOAL_201_1", "영양 목표가 등록되었습니다."),
    GOAL_FETCHED("NUTRITION_GOAL_200_1", "영양 목표가 조회되었습니다."),
    GOAL_UPDATED("NUTRITION_GOAL_200_2", "영양 목표가 수정되었습니다."),
    GOAL_DELETED("NUTRITION_GOAL_200_3", "영양 목표가 삭제되었습니다.");
    private final String code;
    private final String message;
}
