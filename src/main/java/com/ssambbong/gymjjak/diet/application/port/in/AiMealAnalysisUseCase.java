package com.ssambbong.gymjjak.diet.application.port.in;

import com.ssambbong.gymjjak.diet.application.command.AiMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.result.AiMealAnalysisResult;
import reactor.core.publisher.Mono;

public interface AiMealAnalysisUseCase {
    Mono<AiMealAnalysisResult> analyze(AiMealAnalysisCommand command);
}
