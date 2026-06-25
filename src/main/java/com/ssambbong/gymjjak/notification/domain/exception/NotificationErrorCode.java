package com.ssambbong.gymjjak.notification.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    INVALID_NOTIFICATION_REQUEST(
            HttpStatus.BAD_REQUEST,
            "NOTIFICATION_400_1",
            "알림 요청값이 유효하지 않습니다."
    ),

    NOTIFICATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "NOTIFICATION_404_1",
            "알림을 찾을 수 없습니다."
    ),

    FORBIDDEN_NOTIFICATION_ACCESS(
            HttpStatus.FORBIDDEN,
            "NOTIFICATION_403_1",
            "알림에 대한 접근 권한이 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
