package com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BusinessException;

public class PtRecommendationInvalidException extends BusinessException {
    public PtRecommendationInvalidException(String message) {
        super(PtRecommendationErrorCode.INVALID_REQUEST, message);
    }
}
