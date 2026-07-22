package com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BusinessException;

// FastAPI(AI 서버) 호출 관련 예외 — 서버 오류(502)/타임아웃(504) 등 AiPtRecommendationClientAdapter에서 발생.
public class PtRecommendationAiException extends BusinessException {
    public PtRecommendationAiException(PtRecommendationErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public PtRecommendationAiException(PtRecommendationErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
