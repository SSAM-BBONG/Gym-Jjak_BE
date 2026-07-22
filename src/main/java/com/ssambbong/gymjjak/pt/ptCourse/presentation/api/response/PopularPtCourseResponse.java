package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.PartTypeNameMapper;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

public record PopularPtCourseResponse(
        Long ptCourseId,
        String title,
        int price,
        String thumbnailUrl,
        String part,
        String trainerName,
        String roadAddress
) {
    public static PopularPtCourseResponse from(PtCourseQueryUseCase.PopularCourseView view) {
        return new PopularPtCourseResponse(
                view.ptCourseId(),
                view.title(),
                view.price(),
                view.thumbnailUrl(),
                PartTypeNameMapper.toKoreanName(view.part()),
                view.trainerName(),
                view.roadAddress()
        );
    }
}
