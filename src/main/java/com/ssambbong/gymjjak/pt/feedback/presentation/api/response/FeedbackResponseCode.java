package com.ssambbong.gymjjak.pt.feedback.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedbackResponseCode implements ResponseCode {

    STUDENT_FEEDBACKS_FETCHED("STUDENT_FEEDBACKS_200", "수강생 피드백 목록 조회 성공");

    private final String code;
    private final String message;
}
