package com.ssambbong.gymjjak.notification.application.service;

import com.ssambbong.gymjjak.notification.application.port.out.NotificationQueryPort;
import com.ssambbong.gymjjak.notification.application.result.UnreadNotificationCountResult;
import com.ssambbong.gymjjak.notification.domain.exception.InvalidNotificationException;
import com.ssambbong.gymjjak.notification.infrastructure.metrics.NotificationMetric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationQueryPort notificationQueryPort;

    @Mock
    private NotificationMetric notificationMetric;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Test
    void findUnreadNotificationCount_returnsCountFromQueryPort() {
        // 활성 미읽음 알림 수를 Query Port 결과 그대로 반환합니다.
        when(notificationQueryPort.countUnreadNotifications(eq(7L), any(LocalDateTime.class)))
                .thenReturn(3L);

        UnreadNotificationCountResult result =
                notificationQueryService.findUnreadNotificationCount(7L);

        assertThat(result.unreadCount()).isEqualTo(3L);
    }

    @Test
    void findUnreadNotificationCount_throwsWhenReceiverIdIsInvalid() {
        // 유효하지 않은 사용자 ID는 조회 Port 호출 전에 차단합니다.
        assertThatThrownBy(() -> notificationQueryService.findUnreadNotificationCount(0L))
                .isInstanceOf(InvalidNotificationException.class);

        verifyNoInteractions(notificationQueryPort);
    }
}
