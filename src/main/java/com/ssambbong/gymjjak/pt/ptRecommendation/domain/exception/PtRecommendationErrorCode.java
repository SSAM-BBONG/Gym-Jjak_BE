package com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PtRecommendationErrorCode implements ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "PT_RECOMMENDATION_400_1", "요청 값이 올바르지 않습니다."),
    NO_CANDIDATES(HttpStatus.NOT_FOUND, "PT_RECOMMENDATION_404_1", "조건에 맞는 PT코스를 찾지 못했습니다."),
    INVALID_AI_RESULT(HttpStatus.BAD_GATEWAY, "PT_RECOMMENDATION_AI_502_1", "AI PT 추천 결과가 올바르지 않습니다."),
    AI_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "PT_RECOMMENDATION_AI_502_2", "AI PT 추천 서버 호출에 실패했습니다."),
    AI_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "PT_RECOMMENDATION_AI_504_1", "AI PT 추천 요청 시간이 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
