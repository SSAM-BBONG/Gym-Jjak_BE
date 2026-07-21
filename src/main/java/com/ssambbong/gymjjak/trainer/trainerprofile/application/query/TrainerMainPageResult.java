package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

import java.math.BigDecimal;
import java.util.List;

public record TrainerMainPageResult(
        long organizationCount,
        long currentStudentCount,
        BigDecimal averageRating,
        int reviewCount,
        List<InProgressPtCourse> inProgressPtCourses
) {

    public record InProgressPtCourse(
            Long ptCourseId,
            String thumbnailUrl,
            String title,
            String trainerName,
            String organizationName,
            int price,
            long currentStudentCount
    ) {
    }
}
