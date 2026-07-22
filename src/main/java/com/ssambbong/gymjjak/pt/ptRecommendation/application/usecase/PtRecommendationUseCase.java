package com.ssambbong.gymjjak.pt.ptRecommendation.application.usecase;

import com.ssambbong.gymjjak.pt.ptRecommendation.application.command.PtRecommendationCommand;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.result.PtRecommendationResult;

public interface PtRecommendationUseCase {
    PtRecommendationResult recommend(PtRecommendationCommand command);
}
