package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

import java.time.LocalDate;
import java.util.List;

public record PtCourseReservationListResponse(
        String title,
        List<ReservationItem> ptReservations
) {
    public static PtCourseReservationListResponse from(PtCourseQueryUseCase.CourseReservationListView view) {
        return new PtCourseReservationListResponse(
                view.title(),
                view.ptReservations().stream()
                        .map(ReservationItem::from)
                        .toList()
        );
    }
    // 수강생 1명의 예약 정보
    public record ReservationItem(
            Long ptReservationId,
            String nickname,
            String status,
            LocalDate lastPtDate,
            int progressCount,
            int totalSessionCount
    ) {
        public static ReservationItem from(PtCourseQueryUseCase.CourseReservationView view) {
            return new ReservationItem(
                    view.ptReservationId(),
                    view.nickname(),
                    view.status().name(),
                    view.lastPtDate(),
                    view.progressCount(),
                    view.totalSessionCount()
            );
        }
    }
}
