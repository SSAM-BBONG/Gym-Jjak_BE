package com.ssambbong.gymjjak.trainer.routinerecommendation.application.service;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.in.TrainerRoutineRecommendationUseCase;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out.TrainerRoutineAiPort;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerRoutineRecommendationService implements TrainerRoutineRecommendationUseCase {
    private final TrainerRoutineRecommendationSnapshotService snapshotService;
    private final TrainerRoutineAiPort trainerRoutineAiPort;

    // Calls FastAPI only after the snapshot service has completed its read-only transaction.
    @Override
    public TrainerRoutineRecommendationResult recommend(RecommendTrainerRoutineCommand command) {
        TrainerRoutineAiPort.TrainerWorkoutData workoutData = snapshotService.load(command);
        return trainerRoutineAiPort.recommend(command, workoutData);
    }
}
