package com.ssambbong.gymjjak.trainer.routinerecommendation.infrastructure.adapter.out;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGender;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGoal;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out.TrainerRoutineAiPort;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class TrainerRoutineFastApiClientAdapterTest {

    private MockRestServiceServer server;
    private TrainerRoutineFastApiClientAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://fastapi.test");
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new TrainerRoutineFastApiClientAdapter(builder.build());
    }

    @Test
    void sendsRecentWorkoutsAndTwentyEightDaySummaryToFastApi() {
        server.expect(requestTo("http://fastapi.test/api/v1/routines/trainer-analysis"))
                .andExpect(method(POST))
                .andExpect(content().json("""
                        {
                          "subject_user_id": 63,
                          "profile": {
                            "gender": "MALE",
                            "age": 28,
                            "height_cm": 175.5,
                            "weight_kg": 72.3,
                            "goal": "MUSCLE_GAIN"
                          },
                          "recent_workouts": [{
                            "diary_date": "2026-07-25",
                            "part": "CHEST",
                            "exercise": "Bench Press",
                            "sets": [{"set_number": 1, "weight": 60, "reps": 10}]
                          }],
                          "workout_summary": {
                            "period_days": 28,
                            "workout_days": 1,
                            "part_session_counts": {"CHEST": 1},
                            "part_total_volume_kg": {"CHEST": 600}
                          }
                        }
                        """))
                .andRespond(withSuccess("""
                        {
                          "status": "COMPLETE",
                          "title": "주 3회 전신 루틴",
                          "summary": "최근 운동 기록을 반영한 루틴입니다.",
                          "days": [],
                          "cautions": [],
                          "missing_data": [],
                          "sources": []
                        }
                        """, MediaType.APPLICATION_JSON));

        TrainerRoutineRecommendationResult result = adapter.recommend(command(), workoutData());

        assertThat(result.title()).isEqualTo("주 3회 전신 루틴");
        server.verify();
    }

    private RecommendTrainerRoutineCommand command() {
        return new RecommendTrainerRoutineCommand(
                10L, 63L, TrainerRoutineGender.MALE, 28,
                new BigDecimal("175.5"), new BigDecimal("72.3"), TrainerRoutineGoal.MUSCLE_GAIN
        );
    }

    private TrainerRoutineAiPort.TrainerWorkoutData workoutData() {
        return new TrainerRoutineAiPort.TrainerWorkoutData(
                List.of(new TrainerRoutineAiPort.TrainerWorkoutSnapshot(
                        "2026-07-25", "CHEST", "Bench Press",
                        List.of(new TrainerRoutineAiPort.TrainerWorkoutSetSnapshot(1, new BigDecimal("60"), 10))
                )),
                new TrainerRoutineAiPort.TrainerWorkoutSummary(
                        28, 1, Map.of("CHEST", 1), Map.of("CHEST", new BigDecimal("600"))
                )
        );
    }
}
