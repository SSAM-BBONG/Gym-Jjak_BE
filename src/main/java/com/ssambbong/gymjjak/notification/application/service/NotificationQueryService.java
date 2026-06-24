package com.ssambbong.gymjjak.notification.application.service;


import com.ssambbong.gymjjak.notification.application.port.out.NotificationQueryPort;
import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;
import com.ssambbong.gymjjak.notification.application.result.NotificationResult;
import com.ssambbong.gymjjak.notification.application.usecase.NotificationQueryUseCase;
import com.ssambbong.gymjjak.notification.domain.exception.InvalidNotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationQueryService implements NotificationQueryUseCase {

    private final NotificationQueryPort notificationQueryPort;

    @Override
    public NotificationListResult findNotifications(FindNotificationsQuery query) {
        // Query 검증
        validateFindQuery(query);

        log.info(
                "event=notification_find_started, receiverId={}, page={}, size={}",
                query.receiverId(),
                query.page(),
                query.size()
        );

        NotificationListResult result
                = notificationQueryPort.findNotifications(query);

        log.info(
                "event=notification_find_succeeded, receiverId={}, page={}, size={}, resultCount={}, hasNext={}",
                query.receiverId(),
                result.page(),
                result.size(),
                result.content().size(),
                result.hasNext()
        );

        return result;
    }

    // 쿼리 검증
    private void validateFindQuery(FindNotificationsQuery query) {
        if (query == null) {
            throw new InvalidNotificationException(
                    "알림 목록 조회 query는 필수입니다."
            );
        }

        if (query.receiverId() == null || query.receiverId() <= 0) {
            throw new InvalidNotificationException(
                    "receiverId는 1 이상이어야 합니다."
            );
        }

        if (query.page() < 0) {
            throw new InvalidNotificationException(
                    "page는 0 이상이어야 합니다."
            );
        }

        if (query.size() <= 0 || query.size() > 50) {
            throw new InvalidNotificationException(
                    "size는 1 이상 50 이하이어야 합니다."
            );
        }
    }
}
