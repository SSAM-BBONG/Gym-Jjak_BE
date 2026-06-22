package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request;

import com.ssambbong.gymjjak.pt.ptCourse.application.command.UpdatePtCourseCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdatePtCourseRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Long categoryId,
        Long tagId,
        @Min(0) int price,
        Long thumbnailFileId,
        List<CurriculumRequest> curriculums,
        List<ScheduleRequest> schedules
) {
    public record CurriculumRequest(
            Long id,          // 있으면 수정, 없으면 신규
            int sessionNo,
            String title,
            String content
    ) {}

    public record ScheduleRequest(
            Long id,          // 있으면 수정, 없으면 신규
            String dayOfWeek,
            String startTime,
            String endTime
    ) {}

    public UpdatePtCourseCommand toCommand(Long userId, Long ptCourseId) {
        List<UpdatePtCourseCommand.CurriculumData> curriculumData = curriculums == null ? null :
                curriculums.stream()
                        .map(c -> new UpdatePtCourseCommand.CurriculumData(c.id(), c.sessionNo(), c.title(), c.content()))
                        .toList();

        List<UpdatePtCourseCommand.ScheduleData> scheduleData = schedules == null ? null :
                schedules.stream()
                        .map(s -> new UpdatePtCourseCommand.ScheduleData(s.id(), s.dayOfWeek(), s.startTime(), s.endTime()))
                        .toList();

        return new UpdatePtCourseCommand(userId, ptCourseId, title, description, categoryId, tagId, price, thumbnailFileId, curriculumData, scheduleData);
    }
}
