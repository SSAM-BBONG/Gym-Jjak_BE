package com.ssambbong.gymjjak.global.domain.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorCodeTest {

    @DisplayName("INVALID_INPUT 에러 코드는 올바른 상태값, 코드, 메시지를 가진다")
    @Test
    void invalidInputErrorCodeHasExceptedValues() {

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;

        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorCode.getCode()).isEqualTo("COMMON_400");
        assertThat(errorCode.getMessage()).isNotBlank();
    }

    @DisplayName("INTERNAL_SERVER_ERROR 에러 코드는 500 상태를 가진다")
    @Test
    void internalServerErrorHasExpectedStatus() {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(errorCode.getCode()).isEqualTo("COMMON_500");
        assertThat(errorCode.getMessage()).isNotBlank();
    }
}
