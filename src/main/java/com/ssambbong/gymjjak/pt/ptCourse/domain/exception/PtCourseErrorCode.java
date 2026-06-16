package com.ssambbong.gymjjak.pt.ptCourse.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PtCourseErrorCode implements ErrorCode {

    // PT 강습 유효성 검증 실패 (title/description null, price < 0, totalSessionCount < 1)
    PT_COURSE_INVALID(HttpStatus.BAD_REQUEST, "PT_COURSE_001", "PT 강습 정보가 유효하지 않습니다."),

    // PT 강습 조회 실패
    PT_COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "PT_COURSE_002", "PT 강습을 찾을 수 없습니다."),

    // 본인 강습이 아닐 때
    PT_COURSE_FORBIDDEN(HttpStatus.FORBIDDEN, "PT_COURSE_003", "본인의 PT 강습이 아닙니다."),

    // 조직 소속 트레이너가 아닐 때
    PT_COURSE_TRAINER_NOT_IN_ORGANIZATION(HttpStatus.FORBIDDEN, "PT_COURSE_004", "소속 조직이 없는 트레이너입니다."),

    // 트레이너 프로필 조회 실패
    TRAINER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PT_COURSE_005", "트레이너 프로필을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


}
