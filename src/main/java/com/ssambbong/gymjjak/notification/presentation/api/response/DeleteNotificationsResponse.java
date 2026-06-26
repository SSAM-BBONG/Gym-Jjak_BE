package com.ssambbong.gymjjak.notification.presentation.api.response;

import com.ssambbong.gymjjak.notification.application.result.DeleteNotificationsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record DeleteNotificationsResponse(

        @Schema(
                description = "삭제 처리된 알림 ID 목록",
                example = "[1, 2, 3]"
        )
        List<Long> deletedNotificationIds

) {

    public static DeleteNotificationsResponse from(DeleteNotificationsResult result) {
        return DeleteNotificationsResponse.builder()
                .deletedNotificationIds(result.deletedNotificationIds())
                .build();
    }
}
