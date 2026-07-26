package com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrainerRoutineRecommendationErrorCode implements ErrorCode {
    MEMBER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "TRAINER_MEMBER_ACCESS_DENIED", "담당 중인 수강생만 루틴을 추천할 수 있습니다."),
    AI_SERVICE_UNAVAILABLE(HttpStatus.BAD_GATEWAY, "AI_SERVICE_UNAVAILABLE", "AI 루틴 추천 서비스를 일시적으로 사용할 수 없습니다."),
    INVALID_AI_RESULT(HttpStatus.BAD_GATEWAY, "AI_SERVICE_INVALID_RESULT", "AI 루틴 추천 결과가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
