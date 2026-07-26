package com.ssambbong.gymjjak.trainer.routinerecommendation.presentation.api.response;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;

import java.util.List;

public record TrainerRoutineRecommendationResponse(
        String status, String title, String summary, List<Day> days, List<String> cautions,
        List<String> missingData, List<Source> sources
) {
    public static TrainerRoutineRecommendationResponse from(TrainerRoutineRecommendationResult result) {
        return new TrainerRoutineRecommendationResponse(result.status(), result.title(), result.summary(),
                result.days().stream().map(Day::from).toList(), result.cautions(), result.missingData(),
                result.sources().stream().map(Source::from).toList());
    }

    public record Day(String dayLabel, String goal, List<String> warmUp,
                      List<Exercise> exercises, List<String> coolDown
    ) {
        static Day from(TrainerRoutineRecommendationResult.Day day) {
            return new Day(day.dayLabel(),
                    day.goal(),
                    day.warmUp(),
                    day.exercises().stream().map(Exercise::from).toList(),
                    day.coolDown()
            );
        }
    }

    public record Exercise(String name, String part, int sets,
                           String reps, String intensity,
                           int restSeconds, String rationale
    ) {
        static Exercise from(TrainerRoutineRecommendationResult.Exercise exercise) {
            return new Exercise(exercise.name(),
                    exercise.part(),
                    exercise.sets(),
                    exercise.reps(),
                    exercise.intensity(),
                    exercise.restSeconds(),
                    exercise.rationale()
            );
        }
    }

    public record Source(String source, String title, String category) {
        static Source from(TrainerRoutineRecommendationResult.Source source) {
            return new Source(source.source(), source.title(), source.category());
        }
    }
}
