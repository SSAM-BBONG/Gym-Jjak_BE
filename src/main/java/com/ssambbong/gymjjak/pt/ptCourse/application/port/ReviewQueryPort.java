package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewQueryPort {

    // 트레이너 프로필 ID 기준 최근 강사평 조회
    List<ReviewSummary> findRecentByTrainerProfileId(Long trainerProfileId, int limit);

    record ReviewSummary(
            Long reviewId,
            int rating,
            String content,
            LocalDateTime createdAt
    ) {}
}
