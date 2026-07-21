package com.ssambbong.gymjjak.notification.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    TRAINER_APPLICATION_APPROVED(
            NotificationCategory.TRAINER,
            NotificationTargetType.TRAINER_APPLICATION,
            "트레이너 신청 승인 안내",
            "트레이너 신청이 승인되었습니다. 내 트레이너 프로필을 확인해보세요."
    ),

    TRAINER_APPLICATION_REJECTED(
            NotificationCategory.TRAINER,
            NotificationTargetType.TRAINER_APPLICATION,
            "트레이너 신청 반려 안내",
            "트레이너 신청이 반려되었습니다. 반려 사유를 확인해주세요."
    ),

    ORGANIZATION_APPLICATION_APPROVED(
            NotificationCategory.ORGANIZATION,
            NotificationTargetType.ORGANIZATION_APPLICATION,
            "조직 신청 승인 안내",
            "조직 신청이 승인되었습니다. 내 조직을 확인해보세요."
    ),

    ORGANIZATION_APPLICATION_REJECTED(
            NotificationCategory.ORGANIZATION,
            NotificationTargetType.ORGANIZATION_APPLICATION,
            "조직 신청 반려 안내",
            "조직 신청이 반려되었습니다. 반려 사유를 확인해주세요."
    ),

    PT_RESERVATION_REQUESTED(
            NotificationCategory.PT,
            NotificationTargetType.PT_RESERVATION,
            "PT 예약 신청 안내",
            "새로운 PT 예약 신청이 도착했습니다."
    ),

    PT_RESERVATION_APPROVED(
            NotificationCategory.PT,
            NotificationTargetType.PT_RESERVATION,
            "PT 예약 확정 안내",
            "PT 예약이 승인되었습니다."
    ),

    PT_RESERVATION_CANCELED(
            NotificationCategory.PT,
            NotificationTargetType.PT_RESERVATION,
            "예약 취소 안내",
            "PT 수업 예약이 취소되었습니다. 재예약을 진행해주세요."
    ),

    PT_REMINDER(
            NotificationCategory.PT,
            NotificationTargetType.PT_RESERVATION,
            "PT 수업 리마인더",
            "PT 수업이 시작됩니다. 잊지 말고 참석해주세요."
    ),

    FEEDBACK_CREATED(
            NotificationCategory.FEEDBACK,
            NotificationTargetType.FEEDBACK,
            "피드백 등록 안내",
            "새로운 피드백이 등록되었습니다. 피드백을 확인해주세요."
    );

    private final NotificationCategory category;
    private final NotificationTargetType targetType;
    private final String title;
    private final String content;
}
