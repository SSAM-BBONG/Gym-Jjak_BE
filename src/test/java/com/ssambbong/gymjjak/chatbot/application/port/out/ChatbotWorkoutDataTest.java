package com.ssambbong.gymjjak.chatbot.application.port.out;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotWorkoutDataTest {

    @Test
    void keepsOnlyLatestThirtyWorkoutsAndSummarizesAllTwentyEightDayRecords() {
        List<ChatbotUserDataSnapshot.Workout> workouts = new ArrayList<>();
        for (int index = 0; index < 30; index++) {
            workouts.add(workout(index % 28, "CHEST", "Bench Press", "60", 10));
        }
        workouts.add(workout(1, "BACK", "Lat Pulldown", "50", 12));

        ChatbotWorkoutData result = ChatbotWorkoutData.from(workouts, 30, 28);

        assertThat(result.recentWorkouts()).hasSize(30);
        assertThat(result.summary().workoutDays()).isEqualTo(28);
        assertThat(result.summary().partSessionCounts()).containsEntry("CHEST", 30).containsEntry("BACK", 1);
        assertThat(result.summary().partTotalVolumeKg()).containsEntry("CHEST", new BigDecimal("18000"));
        assertThat(result.summary().partTotalVolumeKg()).containsEntry("BACK", new BigDecimal("600"));
    }

    // 동일 일자에 여러 종목이 있어도 운동일 수는 날짜별로 한 번만 집계합니다.
    private ChatbotUserDataSnapshot.Workout workout(
            int daysAgo, String part, String exercise, String weight, int reps
    ) {
        return new ChatbotUserDataSnapshot.Workout(
                LocalDate.now().minusDays(daysAgo), part, exercise,
                List.of(new ChatbotUserDataSnapshot.WorkoutSet(1, new BigDecimal(weight), reps))
        );
    }
}
