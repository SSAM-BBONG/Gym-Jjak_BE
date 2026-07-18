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
    PT_COURSE_TRAINER_NOT_IN_ORGANIZATION(HttpStatus.FORBIDDEN, "PT_COURSE_004", "선택한 조직에 소속되지 않은 트레이너입니다."),

    // 트레이너 프로필 조회 실패
    TRAINER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PT_COURSE_005", "트레이너 프로필을 찾을 수 없습니다."),

    // 트레이너가 설정 불가능한 상태값 요청 시
    PT_COURSE_STATUS_INVALID(HttpStatus.BAD_REQUEST, "PT_COURSE_006", "트레이너는 VISIBLE 또는 HIDDEN만 설정할 수 있습니다."),

    // 수강생(유저) 조회 실패
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PT_COURSE_007", "수강생 정보를 찾을 수 없습니다."),

    // 수강생이 있어 커리큘럼 수정 불가
    CURRICULUM_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "CURRICULUM_UPDATE_NOT_ALLOWED", "수강생이 있어 커리큘럼을 수정할 수 없습니다."),

    // 스케줄 시간 형식 오류
    INVALID_SCHEDULE(HttpStatus.BAD_REQUEST, "INVALID_SCHEDULE", "수업 시간 형식이 올바르지 않습니다."),

    // API 요청 구조 오류 (중복 sessionNo/ID, 빈 스케줄 등 서비스 레이어 입력 검증)
    PT_COURSE_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "PT_COURSE_008", "PT 강습 요청이 유효하지 않습니다."),

    // BLOCKED 상태로 삭제 불가 (관리자 제재 중)
    PT_COURSE_CANNOT_DELETE(HttpStatus.CONFLICT, "PT_COURSE_009", "BLOCKED 상태의 PT 강습은 삭제할 수 없습니다."),

    // 활성 예약(RESERVED/IN_PROGRESS)이 있어 삭제 불가
    PT_COURSE_HAS_ACTIVE_RESERVATION(HttpStatus.CONFLICT, "PT_COURSE_010", "진행 중인 예약이 있어 PT 강습을 삭제할 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


}
