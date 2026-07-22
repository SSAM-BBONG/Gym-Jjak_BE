package com.ssambbong.gymjjak.pt.ptRecommendation.application.port;

import java.util.List;

// FastAPI(Gym-Jjak-AI)의 PT 추천 엔드포인트 호출 Port.
// 1차 필터링(부위·거리)과 온보딩/PT이력 조회는 이미 Spring이 끝낸 뒤,
// 후보·프로필을 한 번에 번들로 실어 보낸다(diet/trainer_report와 동일한 패턴).
public interface PtRecommendationAiPort {

    List<RecommendedCourse> recommend(
            List<CandidateCourse> candidates,
            Profile profile,
            boolean hasPain,
            String painArea,
            String painOnset
    );

    record CandidateCourse(
            Long courseId,
            String courseName,
            Long trainerId,
            String trainerName,
            String bio
    ) {}

    record Profile(
            String exerciseGoal,
            String exercisePeriod,
            String exerciseFrequency,
            String ptHistorySummary
    ) {}

    record RecommendedCourse(
            Long courseId,
            String courseName,
            Long trainerId,
            String trainerName,
            String reason
    ) {}
}
