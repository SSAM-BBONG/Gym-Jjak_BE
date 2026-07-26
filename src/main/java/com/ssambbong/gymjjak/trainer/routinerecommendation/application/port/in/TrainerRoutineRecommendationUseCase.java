package com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.in;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;

public interface TrainerRoutineRecommendationUseCase {
    TrainerRoutineRecommendationResult recommend(RecommendTrainerRoutineCommand command);
}
