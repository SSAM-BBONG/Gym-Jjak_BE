package com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

// 1차 필터링(부위+거리) 결과 후보 PT코스가 하나도 없을 때 발생 — 404.
public class PtRecommendationNotFoundException extends NotFoundException {
    public PtRecommendationNotFoundException() {
        super(PtRecommendationErrorCode.NO_CANDIDATES);
    }
}
