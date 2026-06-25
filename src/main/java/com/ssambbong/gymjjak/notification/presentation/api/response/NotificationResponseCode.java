package com.ssambbong.gymjjak.notification.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationResponseCode implements ResponseCode {

    NOTIFICATION_LIST_FOUND(
            "NOTIFICATION_200_1",
            "알림 목록 조회에 성공했습니다."
    ),

    NOTIFICATION_READ(
            "NOTIFICATION_200_2",
            "알림 읽음 처리가 완료되었습니다."
    ),

    NOTIFICATION_ALL_READ(
            "NOTIFICATION_200_3",
            "모든 알림 읽음 처리가 완료되었습니다."
    ),

    NOTIFICATION_DELETED(
            "NOTIFICATION_200_4",
            "알림 삭제가 완료되었습니다."
    );

    private final String code;
    private final String message;

}
