package com.ssambbong.gymjjak.chatbot.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshot;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotWorkoutData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotPersonalDataPayloadBenchmarkTest {
    private static final int WORKOUT_COUNT = 120;
    private static final int RECENT_WORKOUT_LIMIT = 30;
    private static final int PERIOD_DAYS = 28;
    private static final int WARM_UP_ITERATIONS = 200;
    private static final int MEASURE_ITERATIONS = 1_000;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void comparesRawAndSummarizedWorkoutPayload() throws Exception {
        List<ChatbotUserDataSnapshot.Workout> workouts = workouts();
        RawPersonalDataPayload rawPayload = RawPersonalDataPayload.from(workouts);

        // Summary creation cost is measured separately from JSON serialization.
        BenchmarkResult summaryBuildResult = measure(() -> ChatbotWorkoutData.from(
                workouts, RECENT_WORKOUT_LIMIT, PERIOD_DAYS));
        ChatbotWorkoutData workoutData = ChatbotWorkoutData.from(workouts, RECENT_WORKOUT_LIMIT, PERIOD_DAYS);
        SummarizedPersonalDataPayload summarizedPayload = SummarizedPersonalDataPayload.from(workoutData);

        BenchmarkResult rawSerializationResult = measure(() -> objectMapper.writeValueAsBytes(rawPayload));
        BenchmarkResult summarizedSerializationResult = measure(() -> objectMapper.writeValueAsBytes(summarizedPayload));

        System.out.printf(
                "RAW_PAYLOAD_BENCHMARK workouts=%d sets=%d bytes=%d averageSerializationMicros=%d%n",
                WORKOUT_COUNT, WORKOUT_COUNT * 3, rawSerializationResult.bytes(), rawSerializationResult.averageMicros());
        System.out.printf(
                "SUMMARY_BUILD_BENCHMARK workouts=%d recentWorkouts=%d averageBuildMicros=%d%n",
                WORKOUT_COUNT, workoutData.recentWorkouts().size(), summaryBuildResult.averageMicros());
        System.out.printf(
                "SUMMARY_PAYLOAD_BENCHMARK recentWorkouts=%d bytes=%d averageSerializationMicros=%d%n",
                workoutData.recentWorkouts().size(), summarizedSerializationResult.bytes(),
                summarizedSerializationResult.averageMicros());

        assertThat(summarizedSerializationResult.bytes()).isLessThan(rawSerializationResult.bytes());
        assertThat(workoutData.recentWorkouts()).hasSize(RECENT_WORKOUT_LIMIT);
    }

    // Keeps the benchmark input within the same 28-day period used by the production adapter.
    private List<ChatbotUserDataSnapshot.Workout> workouts() {
        List<ChatbotUserDataSnapshot.Workout> workouts = new ArrayList<>();
        for (int index = 0; index < WORKOUT_COUNT; index++) {
            workouts.add(new ChatbotUserDataSnapshot.Workout(
                    LocalDate.now().minusDays(index / 5),
                    index % 2 == 0 ? "CHEST" : "BACK",
                    "exercise-" + index,
                    List.of(
                            new ChatbotUserDataSnapshot.WorkoutSet(1, new BigDecimal("60"), 10),
                            new ChatbotUserDataSnapshot.WorkoutSet(2, new BigDecimal("55"), 12),
                            new ChatbotUserDataSnapshot.WorkoutSet(3, new BigDecimal("50"), 12)
                    )
            ));
        }
        return workouts;
    }

    // Warms up the JVM and then measures only repeated work of the supplied operation.
    private BenchmarkResult measure(ThrowingSupplier<?> operation) throws Exception {
        for (int index = 0; index < WARM_UP_ITERATIONS; index++) {
            operation.get();
        }

        long startedAt = System.nanoTime();
        Object result = null;
        for (int index = 0; index < MEASURE_ITERATIONS; index++) {
            result = operation.get();
        }
        long elapsedNanos = System.nanoTime() - startedAt;
        int bytes = result instanceof byte[] payload ? payload.length : 0;
        return new BenchmarkResult(bytes, elapsedNanos / MEASURE_ITERATIONS / 1_000);
    }

    private record RawPersonalDataPayload(
            ChatbotUserDataSnapshot.Onboarding onboarding,
            List<ChatbotUserDataSnapshot.Workout> workouts,
            List<ChatbotUserDataSnapshot.Inbody> inbodies
    ) {
        static RawPersonalDataPayload from(List<ChatbotUserDataSnapshot.Workout> workouts) {
            return new RawPersonalDataPayload(
                    ChatbotPersonalDataPayloadBenchmarkTest.onboarding(),
                    workouts,
                    ChatbotPersonalDataPayloadBenchmarkTest.inbodies());
        }
    }

    private record SummarizedPersonalDataPayload(
            ChatbotUserDataSnapshot.Onboarding onboarding,
            @JsonProperty("recent_workouts") List<ChatbotUserDataSnapshot.Workout> recentWorkouts,
            @JsonProperty("workout_summary") WorkoutSummary workoutSummary,
            List<ChatbotUserDataSnapshot.Inbody> inbodies
    ) {
        static SummarizedPersonalDataPayload from(ChatbotWorkoutData workoutData) {
            return new SummarizedPersonalDataPayload(
                    ChatbotPersonalDataPayloadBenchmarkTest.onboarding(),
                    workoutData.recentWorkouts(),
                    WorkoutSummary.from(workoutData.summary()),
                    ChatbotPersonalDataPayloadBenchmarkTest.inbodies());
        }
    }

    private record WorkoutSummary(
            @JsonProperty("period_days") int periodDays,
            @JsonProperty("workout_days") int workoutDays,
            @JsonProperty("part_session_counts") Map<String, Integer> partSessionCounts,
            @JsonProperty("part_total_volume_kg") Map<String, BigDecimal> partTotalVolumeKg
    ) {
        static WorkoutSummary from(ChatbotWorkoutData.Summary summary) {
            return new WorkoutSummary(summary.periodDays(), summary.workoutDays(),
                    summary.partSessionCounts(), summary.partTotalVolumeKg());
        }
    }

    private record BenchmarkResult(int bytes, long averageMicros) {
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private static ChatbotUserDataSnapshot.Onboarding onboarding() {
        return new ChatbotUserDataSnapshot.Onboarding(
                "MUSCLE_GAIN", "OVER_6_MONTHS", "THREE_TO_FOUR", "WEIGHT_TRAINING");
    }

    private static List<ChatbotUserDataSnapshot.Inbody> inbodies() {
        return List.of(new ChatbotUserDataSnapshot.Inbody(
                LocalDate.now().minusDays(25), new BigDecimal("70"),
                new BigDecimal("20"), new BigDecimal("30")));
    }
}
