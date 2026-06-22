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
    COURSE_RESERVATIONS_FETCHED("PT_COURSE_200_RESERVATIONS", "수강생 목록 조회 성공");

    private final String code;
    private final String message;
}
