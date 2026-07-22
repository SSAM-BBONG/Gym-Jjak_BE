package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

public record MyPtCourseListResponse(
        Long ptCourseId,
        String thumbnailUrl,
        String title,
        String trainerName,
        String status,               // VISIBLE / HIDDEN
        int activeReservationCount,  // 현재 수강 중인 수강생 수
        int totalReservationCount    // 전체 수강생 수 (취소 제외)
) {
    public static MyPtCourseListResponse from(PtCourseQueryUseCase.MyPtCourseListView view) {
        return new MyPtCourseListResponse(
                view.ptCourseId(),
                view.thumbnailUrl(),
                view.title(),
                view.trainerName(),
                view.status().name(),
                view.activeReservationCount(),
                view.totalReservationCount()
        );
    }
}
