package com.ssambbong.gymjjak.chatbot.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** FastAPI에 요청 단위로 전달하는 개인 운동 데이터. 저장하지 않는다. */
public record ChatbotUserDataSnapshot(
        Onboarding onboarding,
        ChatbotWorkoutData workoutData,
        List<Inbody> inbodies
) {
    // 개인 데이터 없을 경우 빈 스냅샷 생성 메서드
    public static ChatbotUserDataSnapshot empty() {
        return new ChatbotUserDataSnapshot(null,
                new ChatbotWorkoutData(List.of(), new ChatbotWorkoutData.Summary(28, 0, java.util.Map.of(), java.util.Map.of())),
                List.of());
    }

    public record Onboarding(
            String exerciseGoal,
            String exercisePeriod,
            String exerciseFrequency,
            String preferredExercise
    ) {
    }

    public record Workout(LocalDate diaryDate, String part, String exercise, List<WorkoutSet> sets) {
    }

    public record WorkoutSet(int setNumber, BigDecimal weight, int reps) {
    }

    public record Inbody(
            LocalDate measuredAt,
            BigDecimal weight,
            BigDecimal bodyFatPercentage,
            BigDecimal skeletalMuscleMass
    ) {
    }
}
