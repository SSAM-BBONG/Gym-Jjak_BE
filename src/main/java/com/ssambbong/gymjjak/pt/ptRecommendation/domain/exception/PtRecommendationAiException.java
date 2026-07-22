package com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BusinessException;

public class PtRecommendationAiException extends BusinessException {
    public PtRecommendationAiException(PtRecommendationErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public PtRecommendationAiException(PtRecommendationErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
