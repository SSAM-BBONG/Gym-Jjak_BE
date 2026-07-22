package com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PtRecommendationResponseCode implements ResponseCode {
    PT_RECOMMENDATION_FETCHED("PT_RECOMMENDATION_200", "PT 추천 조회 성공");

    private final String code;
    private final String message;
}
