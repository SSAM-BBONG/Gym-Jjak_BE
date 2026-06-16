package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

// 필수 자격증 OCR 검증 실패 예외
public class RequiredCertificationNotVerifiedException extends BadRequestException {

    public RequiredCertificationNotVerifiedException(Long applicantUserId, Long certificateFileId) {
        super(TrainerApplicationErrorCode.REQUIRED_CERTIFICATION_NOT_VERIFIED);
        addContext("applicantUserId", applicantUserId);
        addContext("certificateFileId", certificateFileId);
    }
}
