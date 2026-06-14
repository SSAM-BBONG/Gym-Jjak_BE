package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PtCourseResponseCode implements ResponseCode {
    PT_COURSE_CREATED("PT_COURSE_201", "PT 강습 등록 성공"),
    PT_COURSE_LIST("PT_COURSE_200_LIST", "PT 강습 목록 조회 성공"),
    PT_COURSE_DETAIL("PT_COURSE_200_DETAIL", "PT 강습 상세 조회 성공");

    private final String code;
    private final String message;
}
