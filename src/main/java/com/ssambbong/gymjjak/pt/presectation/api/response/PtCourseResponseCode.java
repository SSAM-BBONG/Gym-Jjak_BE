package com.ssambbong.gymjjak.pt.presectation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PtCourseResponseCode implements ResponseCode {
    PT_COURSE_CREATED("PT_COURSE_001", "PT 강습 등록 성공");

    private final String code;
    private final String message;
}
