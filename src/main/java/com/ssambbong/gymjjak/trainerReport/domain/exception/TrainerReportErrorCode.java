package com.ssambbong.gymjjak.trainerReport.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrainerReportErrorCode implements ErrorCode {
    INVALID_AI_RESULT(HttpStatus.BAD_GATEWAY, "TRAINER_REPORT_AI_502_1", "AI 트레이너 리포트 결과가 올바르지 않습니다."),
    AI_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "TRAINER_REPORT_AI_502_2", "AI 트레이너 리포트 서버 호출에 실패했습니다."),
    AI_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "TRAINER_REPORT_AI_504_1", "AI 트레이너 리포트 요청 시간이 초과되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "TRAINER_REPORT_404_1", "트레이너 리포트를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
