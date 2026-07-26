package com.ssambbong.gymjjak.trainer.routinerecommendation.infrastructure.adapter.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out.TrainerRoutineAiPort;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;
import com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationErrorCode;
import com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.util.List;

@Component
@Slf4j
public class TrainerRoutineFastApiClientAdapter implements TrainerRoutineAiPort {
    private static final String PATH = "/api/v1/routines/trainer-analysis";
    private final RestClient restClient;
    private final TrainerRoutineAiCallPolicy callPolicy;

    public TrainerRoutineFastApiClientAdapter(@Qualifier("aiServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
        this.callPolicy = new TrainerRoutineAiCallPolicy();
    }

    @Override
    public TrainerRoutineRecommendationResult recommend(
            RecommendTrainerRoutineCommand command, TrainerWorkoutData workoutData
    ) {
        try {
            FastApiRoutineResponse response = callPolicy.execute(() -> requestFastApi(command, workoutData));

            if (response == null || response.title() == null || response.days() == null) {
                throw new TrainerRoutineRecommendationException(TrainerRoutineRecommendationErrorCode.INVALID_AI_RESULT);
            }
            return response.toResult();
        } catch (TrainerRoutineAiCallPolicy.CircuitOpenFailure
                 | TrainerRoutineAiCallPolicy.ConnectionFailure
                 | TrainerRoutineAiCallPolicy.ResponseFailure exception) {
            throw new TrainerRoutineRecommendationException(TrainerRoutineRecommendationErrorCode.AI_SERVICE_UNAVAILABLE, exception);
        } catch (TrainerRoutineRecommendationException exception) {
            throw exception;
        }
    }

    // Converts transport failures into retryable connection failures or non-retryable response failures.
    private FastApiRoutineResponse requestFastApi(
            RecommendTrainerRoutineCommand command, TrainerWorkoutData workoutData
    ) {
        try {
            return restClient.post().uri(PATH)
                    .body(FastApiRoutineRequest.from(command, workoutData))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, result) -> {
                        log.warn("event=trainer_routine_fastapi_failed status={} memberId={}", result.getStatusCode(), command.memberUserId());
                        throw new TrainerRoutineAiCallPolicy.ResponseFailure();
                    })
                    .body(FastApiRoutineResponse.class);
        } catch (RestClientException exception) {
            if (exception instanceof ResourceAccessException resourceAccessException
                    && hasConnectionFailure(resourceAccessException)) {
                throw new TrainerRoutineAiCallPolicy.ConnectionFailure(exception);
            }
            throw new TrainerRoutineAiCallPolicy.ResponseFailure(exception);
        }
    }

    // Read timeouts are deliberately excluded because FastAPI may already be executing the LLM call.
    private boolean hasConnectionFailure(ResourceAccessException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof ConnectException || cause instanceof HttpConnectTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private record FastApiRoutineRequest(
            @JsonProperty("subject_user_id") Long subjectUserId,
            Profile profile,
            @JsonProperty("recent_workouts") List<Workout> recentWorkouts,
            @JsonProperty("workout_summary") WorkoutSummary workoutSummary
    ) {
        static FastApiRoutineRequest from(RecommendTrainerRoutineCommand command, TrainerWorkoutData workoutData) {
            return new FastApiRoutineRequest(command.memberUserId(),
                    new Profile(command.gender().name(), command.age(), command.heightCm(), command.weightKg(), command.goal().name()),
                    workoutData.recentWorkouts().stream().map(Workout::from).toList(),
                    new WorkoutSummary(workoutData.summary().periodDays(), workoutData.summary().workoutDays(),
                            workoutData.summary().partSessionCounts(), workoutData.summary().partTotalVolumeKg()));
        }
    }

    private record WorkoutSummary(@JsonProperty("period_days") int periodDays,
                                  @JsonProperty("workout_days") int workoutDays,
                                  @JsonProperty("part_session_counts") java.util.Map<String, Integer> partSessionCounts,
                                  @JsonProperty("part_total_volume_kg") java.util.Map<String, BigDecimal> partTotalVolumeKg) {}

    private record Profile(String gender, int age, @JsonProperty("height_cm") BigDecimal heightCm,
                           @JsonProperty("weight_kg") BigDecimal weightKg, String goal) {}

    private record Workout(@JsonProperty("diary_date") String diaryDate, String part, String exercise, List<WorkoutSet> sets) {
        static Workout from(TrainerWorkoutSnapshot value) {
            return new Workout(value.diaryDate(), value.part(), value.exercise(),
                    value.sets().stream().map(set -> new WorkoutSet(set.setNumber(), set.weight(), set.reps())).toList());
        }
    }

    private record WorkoutSet(@JsonProperty("set_number") int setNumber,
                              BigDecimal weight, int reps) {}

    private record FastApiRoutineResponse(String status, String title, String summary,
                                          List<Day> days, List<String> cautions,
                                          @JsonProperty("missing_data") List<String> missingData,
                                          List<Source> sources
    ) {
        TrainerRoutineRecommendationResult toResult() {
            return new TrainerRoutineRecommendationResult(status, title, summary,
                    days.stream().map(Day::toResult).toList(), cautions, missingData,
                    sources.stream().map(source -> new TrainerRoutineRecommendationResult.Source(source.source(), source.title(), source.category())).toList());
        }
    }

    private record Day(@JsonProperty("day_label") String dayLabel, String goal, @JsonProperty("warm_up") List<String> warmUp,
                       List<Exercise> exercises, @JsonProperty("cool_down") List<String> coolDown) {
        TrainerRoutineRecommendationResult.Day toResult() {
            return new TrainerRoutineRecommendationResult.Day(dayLabel, goal, warmUp,
                    exercises.stream().map(Exercise::toResult).toList(), coolDown);
        }
    }

    private record Exercise(String name, String part, int sets, String reps, String intensity,
                            @JsonProperty("rest_seconds") int restSeconds, String rationale) {
        TrainerRoutineRecommendationResult.Exercise toResult() {
            return new TrainerRoutineRecommendationResult.Exercise(name, part, sets, reps, intensity, restSeconds, rationale);
        }
    }

    private record Source(String source, String title, String category) {}
}
