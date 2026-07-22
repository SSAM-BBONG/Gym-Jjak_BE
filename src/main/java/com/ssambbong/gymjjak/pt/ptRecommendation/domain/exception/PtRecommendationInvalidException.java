package com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BusinessException;

// 요청 값 자체의 모순(예: hasPain=false인데 painArea가 채워짐) 검증 실패 시 사용 — 400.
public class PtRecommendationInvalidException extends BusinessException {
    public PtRecommendationInvalidException(String message) {
        super(PtRecommendationErrorCode.INVALID_REQUEST, message);
    }
}
