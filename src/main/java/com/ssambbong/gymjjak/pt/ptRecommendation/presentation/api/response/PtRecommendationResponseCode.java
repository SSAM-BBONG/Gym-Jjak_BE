package com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

// PT추천 성공 응답 코드. 실패 시 코드는 PtRecommendationErrorCode를 따로 쓴다.
@Getter
@AllArgsConstructor
public enum PtRecommendationResponseCode implements ResponseCode {
    PT_RECOMMENDATION_FETCHED("PT_RECOMMENDATION_200", "PT 추천 조회 성공");

    private final String code;
    private final String message;
}
