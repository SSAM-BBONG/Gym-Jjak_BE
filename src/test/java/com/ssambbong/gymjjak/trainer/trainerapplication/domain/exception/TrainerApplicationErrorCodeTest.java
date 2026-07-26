package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerApplicationErrorCodeTest {

    @Test
    void requiredCertificationNotVerified_usesClientGuidanceMessage() {
        TrainerApplicationErrorCode errorCode =
                TrainerApplicationErrorCode.REQUIRED_CERTIFICATION_NOT_VERIFIED;

        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getCode()).isEqualTo("TRAINER_APPLICATION_400_2");
        assertThat(errorCode.getMessage())
                .isEqualTo("필수 자격증을 확인할 수 없습니다. 올바른 자격증 이미지를 업로드해 주세요.");
    }
}
