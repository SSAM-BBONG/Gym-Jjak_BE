package com.ssambbong.gymjjak.pt.ptRecommendation.application.usecase;

import com.ssambbong.gymjjak.pt.ptRecommendation.application.command.PtRecommendationCommand;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.result.PtRecommendationResult;

// PT추천 진입점(port-in). 구독 여부와 무관하게 전 회원이 호출 가능하다.
public interface PtRecommendationUseCase {
    PtRecommendationResult recommend(PtRecommendationCommand command);
}
