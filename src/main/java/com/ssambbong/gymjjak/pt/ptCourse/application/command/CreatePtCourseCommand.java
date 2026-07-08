package com.ssambbong.gymjjak.pt.ptCourse.application.command;

import java.util.List;

public record CreatePtCourseCommand(
        Long userId,
        Long partId,
        String title,
        String description,
        int price,
        UploadedFileMetadataCommand thumbnailFile,
        List<CurriculumData> curriculums,
        List<ScheduleData> schedules
) {
    public record CurriculumData(Integer sessionNo, String title, String content) {}
    public record ScheduleData(String dayOfWeek, String startTime, String endTime) {}
}
