package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

public record PtCourseReservationDetailResponse(
        String nickname,
        String email,
        String phone,
        String status,
        int progressCount,
        int totalSessionCount,
        String title
) {
    public static PtCourseReservationDetailResponse from(PtCourseQueryUseCase.ReservationDetailView view) {
        return new PtCourseReservationDetailResponse(
                view.nickname(),
                view.email(),
                view.phone(),
                view.status().name(),
                view.progressCount(),
                view.totalSessionCount(),
                view.title()
        );
    }
}
