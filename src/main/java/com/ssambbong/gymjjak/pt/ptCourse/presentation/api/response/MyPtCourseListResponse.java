package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

public record MyPtCourseListResponse(
        Long ptCourseId,
        Long thumbnailFileId,
        String title,
        String trainerName,
        String status,               // VISIBLE / HIDDEN
        int activeReservationCount,  // RESERVED + IN_PROGRESS 수
        int totalReservationCount    // 전체 예약 수
) {
    public static MyPtCourseListResponse from(PtCourseQueryUseCase.MyPtCourseListView view) {
        return new MyPtCourseListResponse(
                view.ptCourseId(),
                view.thumbnailFileId(),
                view.title(),
                view.trainerName(),
                view.status().name(),
                view.activeReservationCount(),
                view.totalReservationCount()
        );
    }
}
