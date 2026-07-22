package com.ssambbong.gymjjak.diet.application.port.in;

import com.ssambbong.gymjjak.diet.application.command.AiMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.result.AiMealAnalysisResult;

public interface AiMealAnalysisUseCase {
    AiMealAnalysisResult analyze(AiMealAnalysisCommand command);
}
