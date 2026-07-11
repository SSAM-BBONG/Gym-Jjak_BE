package com.ssambbong.gymjjak.exercise.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExerciseResponseCode implements ResponseCode {

    EXERCISE_CREATED("EXERCISE_CREATED", "운동 종목이 등록되었습니다."),
    EXERCISE_UPDATED("EXERCISE_UPDATED", "운동 종목이 수정되었습니다."),
    EXERCISE_DELETED("EXERCISE_DELETED", "운동 종목이 삭제되었습니다."),
    EXERCISES_FETCHED("EXERCISES_FETCHED", "운동 종목 조회가 완료되었습니다.");

    private final String code;
    private final String message;
}
