package com.ssambbong.gymjjak.pt.ptCourse.application.command;

import java.util.List;

public record UpdatePtCourseCommand(
        Long userId,
        Long ptCourseId,
        String title,
        String description,
        Long categoryId,
        Long tagId,
        int price,
        Long thumbnailFileId,
        List<CurriculumData> curriculums,
        List<ScheduleData> schedules
) {
    // id 있으면 수정, 없으면 신규 추가, 요청 누락 시 삭제
    public record CurriculumData(Long id, int sessionNo, String title, String content) {}

    // id 있으면 수정, 없으면 신규 추가, 요청 누락 시 삭제
    public record ScheduleData(Long id, String dayOfWeek, String startTime, String endTime) {}
}
