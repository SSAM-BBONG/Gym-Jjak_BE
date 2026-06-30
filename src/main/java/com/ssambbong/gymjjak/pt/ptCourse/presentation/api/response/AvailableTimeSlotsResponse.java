package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AvailableTimeSlotsResponse(LocalDate date, List<TimeSlot> timeSlots) {

    public record TimeSlot(LocalTime startTime, LocalTime endTime, boolean available) {}

    public static AvailableTimeSlotsResponse from(PtCourseQueryUseCase.AvailableTimeSlotsView view) {
        return new AvailableTimeSlotsResponse(
                view.date(),
                view.timeSlots().stream()
                        .map(s -> new TimeSlot(s.startTime(), s.endTime(), s.available()))
                        .toList()
        );
    }
}
