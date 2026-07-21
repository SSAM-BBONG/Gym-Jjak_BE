package com.ssambbong.gymjjak.trainer.trainerprofile.application.query;

import java.math.BigDecimal;
import java.util.List;

public record TrainerMainPageResult(
        // 소속 조직 수
        long organizationCount,
        // 내 pt 수강 중 수강생
        long currentStudentCount,
        //평균 별전
        BigDecimal averageRating,
        // 리뷰 개수
        int reviewCount,
        // 진행 중인 pt 수 4개
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
