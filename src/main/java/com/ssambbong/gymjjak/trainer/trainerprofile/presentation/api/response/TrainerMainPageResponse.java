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

    // Application 조회 결과를 트레이너 메인 페이지 API 응답으로 변환합니다.
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
