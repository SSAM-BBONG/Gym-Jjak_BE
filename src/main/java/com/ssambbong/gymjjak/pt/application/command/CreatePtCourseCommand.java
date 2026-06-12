package com.ssambbong.gymjjak.pt.application.command;

import java.util.List;

public record CreatePtCourseCommand(
        Long userId,
        Long categoryId,
        Long tagId,
        String title,
        String description,
        int price,
        String thumbnailUrl,
        int sessionDuration,
        List<CurriculumData> curriculums,
        List<ScheduleData> schedules
) {
    public record CurriculumData(String title, String content) {}
    public record ScheduleData(String dayOfWeek, String startTime, String endTime) {}
}
