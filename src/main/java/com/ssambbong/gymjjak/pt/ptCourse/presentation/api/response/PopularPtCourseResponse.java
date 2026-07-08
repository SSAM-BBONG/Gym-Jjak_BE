package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

public record PopularPtCourseResponse(
        Long ptCourseId,
        String title,
        int price,
        String thumbnailUrl,
        Long partId,
        String partName,
        String trainerName,
        String roadAddress
) {
    public static PopularPtCourseResponse from(PtCourseQueryUseCase.PopularCourseView view) {
        return new PopularPtCourseResponse(
                view.ptCourseId(),
                view.title(),
                view.price(),
                view.thumbnailUrl(),
                view.partId(),
                view.partName(),
                view.trainerName(),
                view.roadAddress()
        );
    }
}
