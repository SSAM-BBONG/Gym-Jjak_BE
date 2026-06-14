package com.ssambbong.gymjjak.pt.ptCourse.application.command;

import java.util.List;

public record CreatePtCourseCommand(
        Long userId,
        Long categoryId,
        Long tagId,
        String title,
        String description,
        int price,
        Long thumbnailFileId,
        List<CurriculumData> curriculums,
        List<ScheduleData> schedules
) {
    public record CurriculumData(Integer sessionNo, String title, String content) {}
    public record ScheduleData(String dayOfWeek, String startTime, String endTime) {}
}
