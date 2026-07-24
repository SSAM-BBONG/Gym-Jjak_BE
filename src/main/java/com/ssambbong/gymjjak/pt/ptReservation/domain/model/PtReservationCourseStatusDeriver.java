package com.ssambbong.gymjjak.pt.ptReservation.domain.model;

import java.util.List;

// 유저+코스 단위 회차 목록으로 코스 집계 상태를 계산하는 공용 로직.
// PtReservationQueryService / PtCourseQueryService 양쪽에서 재사용한다 — 각자 복붙하면
// 한쪽만 고치고 다른 쪽은 버그가 남는 문제가 반복돼서(anyInProgress 누락 등) 여기로 모았다.
public final class PtReservationCourseStatusDeriver {

    private PtReservationCourseStatusDeriver() {
    }

    public static PtReservationStatus derive(List<PtReservation> sessions, int progressCount, int totalSessionCount) {
        boolean allCancelled = sessions.stream().allMatch(r -> r.getStatus() == PtReservationStatus.CANCELLED);
        boolean allCompleted = sessions.stream().allMatch(r -> r.getStatus() == PtReservationStatus.COMPLETED);
        boolean anyInProgress = sessions.stream().anyMatch(r -> r.getStatus() == PtReservationStatus.IN_PROGRESS);

        if (allCancelled) return PtReservationStatus.CANCELLED;
        if (allCompleted || progressCount >= totalSessionCount) return PtReservationStatus.COMPLETED;
        if (progressCount == 0 && !anyInProgress) return PtReservationStatus.RESERVED;
        return PtReservationStatus.IN_PROGRESS;
    }
}
