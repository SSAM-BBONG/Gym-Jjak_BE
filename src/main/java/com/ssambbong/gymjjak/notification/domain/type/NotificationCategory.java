package com.ssambbong.gymjjak.notification.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 큰 분류 라벨 : [PT], [조직]
@Getter
@RequiredArgsConstructor
public enum NotificationCategory {

    PT("PT"),
    FEEDBACK("피드백"),
    ORGANIZATION("조직"),
    TRAINER("트레이너");

    private final String label;
}
