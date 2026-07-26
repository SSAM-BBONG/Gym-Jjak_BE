package com.ssambbong.gymjjak.trainer.routinerecommendation.application.result;

import java.util.List;

public record TrainerRoutineRecommendationResult(
        String status,
        String title,
        String summary,
        List<Day> days,
        List<String> cautions,
        List<String> missingData,
        List<Source> sources
) {
    public record Day(String dayLabel, String goal, List<String> warmUp, List<Exercise> exercises, List<String> coolDown) {
    }
    public record Exercise(String name, String part, int sets, String reps, String intensity, int restSeconds, String rationale) {
    }
    public record Source(String source, String title, String category) {
    }
}
