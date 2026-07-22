package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request;

import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UpdatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record UpdatePtCourseRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull PartType part,
        @Min(0) int price,
        @Valid UploadedFileMetadataRequest thumbnailFile,

        @Valid
        List<CurriculumRequest> curriculums,

        @Valid
        List<ScheduleRequest> schedules
) {
    public record CurriculumRequest(
            Long id,              // 있으면 수정, 없으면 신규
            @NotNull @Min(1) Integer sessionNo,
            @NotBlank String title,
            String content
    ) {}

    public record ScheduleRequest(
            Long id,              // 있으면 수정, 없으면 신규
            @NotNull
            @Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY",
                    message = "요일은 MONDAY~SUNDAY 중 하나여야 합니다.")
            String dayOfWeek,
            @NotBlank
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "시작 시간은 HH:mm 형식이어야 합니다.")
            String startTime,
            @NotBlank
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "종료 시간은 HH:mm 형식이어야 합니다.")
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

        UploadedFileMetadataCommand thumbnailFileCommand = thumbnailFile == null ? null :
                new UploadedFileMetadataCommand(
                        thumbnailFile.fileKey(), thumbnailFile.originalName(),
                        thumbnailFile.contentType(), thumbnailFile.fileSize());
        return new UpdatePtCourseCommand(userId, ptCourseId, title, description, part, price, thumbnailFileCommand, curriculumData, scheduleData);
    }
}
