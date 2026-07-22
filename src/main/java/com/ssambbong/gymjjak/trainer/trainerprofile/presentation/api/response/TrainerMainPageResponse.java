package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerMainPageResult;

import java.math.BigDecimal;
import java.util.List;

public record TrainerMainPageResponse(
        long organizationCount,
        long currentStudentCount,
        BigDecimal averageRating,
        int reviewCount,
        List<InProgressPtCourseResponse> inProgressPtCourses
) {

    public static TrainerMainPageResponse from(TrainerMainPageResult result) {
        return new TrainerMainPageResponse(
                result.organizationCount(),
                result.currentStudentCount(),
                result.averageRating(),
                result.reviewCount(),
                result.inProgressPtCourses().stream()
                        .map(InProgressPtCourseResponse::from)
                        .toList()
        );
    }

    // 해당 메서드에서만 사용하기 때문에 내부 record로 구현
    public record InProgressPtCourseResponse(
            Long ptCourseId,
            String thumbnailUrl,
            String title,
            String trainerName,
            String organizationName,
            int price,
            long currentStudentCount
    ) {

        // Application 카드 결과를 API 카드 응답으로 변환합니다.
        private static InProgressPtCourseResponse from(
                TrainerMainPageResult.InProgressPtCourse result
        ) {
            return new InProgressPtCourseResponse(
                    result.ptCourseId(),
                    result.thumbnailUrl(),
                    result.title(),
                    result.trainerName(),
                    result.organizationName(),
                    result.price(),
                    result.currentStudentCount()
            );
        }
    }
}
