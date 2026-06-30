package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PtCourseResponseCode implements ResponseCode {
    PT_COURSE_CREATED("PT_COURSE_201", "PT 강습 등록 성공"),
    PT_COURSE_LIST("PT_COURSE_200_LIST", "PT 강습 목록 조회 성공"),
    PT_COURSE_DETAIL("PT_COURSE_200_DETAIL", "PT 강습 상세 조회 성공"),
    PT_COURSE_STATUS_UPDATED("PT_COURSE_200_STATUS", "PT 강습 상태 변경 성공"),
    MY_PT_COURSES_FETCHED("PT_COURSE_200_MY_LIST", "내 강습 목록 조회 성공"),
    COURSE_RESERVATIONS_FETCHED("PT_COURSE_200_RESERVATIONS", "수강생 목록 조회 성공"),
    STUDENT_DETAIL_FETCHED("PT_COURSE_200_STUDENT_DETAIL", "수강생 상세 조회 성공"),
    PT_COURSE_UPDATED("PT_COURSE_UPDATED", "PT 강습 수정 성공"),
    PT_COURSE_DELETED("PT_COURSE_DELETED", "PT 강습 삭제 성공"),
    PT_COURSE_POPULAR("PT_COURSE_POPULAR", "인기 강습 조회 성공"),
    PT_STATS("PT_STATS", "PT 통계 조회 성공"),
    AVAILABLE_DATES_FETCHED("PT_COURSE_200_AVAILABLE_DATES", "예약 가능 날짜 조회 성공"),
    AVAILABLE_TIME_SLOTS_FETCHED("PT_COURSE_200_AVAILABLE_TIME_SLOTS", "예약 가능 시간 슬롯 조회 성공");

    private final String code;
    private final String message;
}
