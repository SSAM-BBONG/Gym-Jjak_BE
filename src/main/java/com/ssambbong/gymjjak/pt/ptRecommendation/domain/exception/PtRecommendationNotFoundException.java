package com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class PtRecommendationNotFoundException extends NotFoundException {
    public PtRecommendationNotFoundException() {
        super(PtRecommendationErrorCode.NO_CANDIDATES);
    }
}
