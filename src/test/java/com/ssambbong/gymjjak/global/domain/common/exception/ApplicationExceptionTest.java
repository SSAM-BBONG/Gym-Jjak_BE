package com.ssambbong.gymjjak.global.domain.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationExceptionTest {

    public static class TestApplicationException extends ApplicationException {

        protected TestApplicationException(ErrorCode errorCode, String message) {
            super(errorCode, message);
        }

        protected TestApplicationException(ErrorCode errorCode, String message, Throwable cause) {
            super(errorCode, message, cause);
        }

        // 실제 addContext() protected 여서 putContext로 가져와서 구현함
        void putContext(String key, Object value) {
            addContext(key, value);
        }
    }

    @DisplayName("ApplicationException은 ErrorCode와 message를 보관한다")
    @Test
    void applicationException_ErrorCode_message_보관_테스트() {

        TestApplicationException exception =
                new TestApplicationException(ErrorCode.INVALID_INPUT, "잘못된 요청입니다.");

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
        assertThat(exception.getMessage()).isEqualTo("잘못된 요청입니다.");
    }

    @DisplayName("ApplicationException은 Context 정보를 가질 수 있다.")
    @Test
    void applicationException_Context_포함_여부_테스트() {

        TestApplicationException exception
                = new TestApplicationException(ErrorCode.REPORT_GROUP_NOT_FOUND, "신고 그룹을 찾을 수 없습니다.");

        exception.putContext("reportGroupId", 999L);

        assertThat(exception.getContext()).containsEntry("reportGroupId", 999L);
    }

    @DisplayName("ApplicationException은 원인 예외를 함께 보관할 수 있다")
    @Test
    void applicationException_원인_포함_가능_테스트() {
        RuntimeException cause = new RuntimeException("db error");

        TestApplicationException exception =
                new TestApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류", cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        assertThat(exception.getMessage()).isEqualTo("서버 오류");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
