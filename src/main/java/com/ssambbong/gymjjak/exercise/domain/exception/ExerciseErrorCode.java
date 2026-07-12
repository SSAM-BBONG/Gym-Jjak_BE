package com.ssambbong.gymjjak.exercise.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExerciseErrorCode implements ErrorCode {

    EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE_404_001", "운동 종목을 찾을 수 없습니다."),
    EXERCISE_DUPLICATED(HttpStatus.CONFLICT, "EXERCISE_409_001", "이미 등록된 운동 종목입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
