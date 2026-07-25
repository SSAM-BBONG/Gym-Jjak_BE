package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewQueryPort {

    // 트레이너 프로필 ID 기준 최근 강사평 조회
    List<ReviewSummary> findRecentByTrainerProfileId(Long trainerProfileId, int limit);

    // PT 강습 ID 기준 최근 강사평 조회 (PT 상세 미리보기 전용)
    List<ReviewSummary> findRecentByPtCourseId(Long ptCourseId, int limit);

    record ReviewSummary(
            @Schema(description = "강사평 ID", example = "1")
            Long reviewId,
            @Schema(description = "작성자 닉네임", example = "홍길동")
            String nickname,
            @Schema(description = "별점 (1~5)", example = "5")
            int rating,
            @Schema(description = "강사평 내용", example = "정말 친절하고 전문적인 트레이너입니다.")
            String content,
            @Schema(description = "작성일시")
            LocalDateTime createdAt
    ) {}
}
