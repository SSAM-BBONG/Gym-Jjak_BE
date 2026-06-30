package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

import java.time.LocalDate;
import java.util.List;

public record AvailableDatesResponse(List<LocalDate> availableDates) {

    public static AvailableDatesResponse from(PtCourseQueryUseCase.AvailableDatesView view) {
        return new AvailableDatesResponse(view.availableDates());
    }
}
