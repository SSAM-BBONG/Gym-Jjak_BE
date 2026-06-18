package com.ssambbong.gymjjak.pt.feedback.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedbackResponseCode implements ResponseCode {

    RESERVATION_FEEDBACKS_FETCHED("RESERVATION_FEEDBACKS_200", "피드백 목록 조회 성공"),
    FEEDBACK_DETAIL_FETCHED("FEEDBACK_DETAIL_200", "피드백 상세 조회 성공");

    private final String code;
    private final String message;
}
