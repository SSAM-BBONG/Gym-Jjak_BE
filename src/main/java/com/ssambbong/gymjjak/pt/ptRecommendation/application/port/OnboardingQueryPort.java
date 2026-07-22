package com.ssambbong.gymjjak.pt.ptRecommendation.application.port;

import java.math.BigDecimal;

// onboarding은 pt.* 밖의 별개 최상위 도메인이라, OnboardingUsecase(등록/수정 포함 전체 인터페이스)를
// 직접 의존하지 않고 PT추천에 필요한 조회 하나만 이 도메인 안에 자체 Port로 정의한다
// (trainerReport가 MyPtCourseQueryPort를 자체 정의한 것과 동일한 패턴).
public interface OnboardingQueryPort {

    MyOnboardingInfo findMyOnboarding(Long userId);

    record MyOnboardingInfo(
            String exerciseGoal,
            String exercisePeriod,
            String exerciseFrequency,
            BigDecimal regionLatitude,
            BigDecimal regionLongitude
    ) {}
}
