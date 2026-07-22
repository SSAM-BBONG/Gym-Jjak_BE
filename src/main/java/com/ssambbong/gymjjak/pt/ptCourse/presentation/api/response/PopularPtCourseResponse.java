package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

public record PopularPtCourseResponse(
        Long ptCourseId,
        String title,
        int price,
        String thumbnailUrl,
        PartType part,
        String trainerName,
        String roadAddress
) {
    public static PopularPtCourseResponse from(PtCourseQueryUseCase.PopularCourseView view) {
        return new PopularPtCourseResponse(
                view.ptCourseId(),
                view.title(),
                view.price(),
                view.thumbnailUrl(),
                view.part(),
                view.trainerName(),
                view.roadAddress()
        );
    }
}
