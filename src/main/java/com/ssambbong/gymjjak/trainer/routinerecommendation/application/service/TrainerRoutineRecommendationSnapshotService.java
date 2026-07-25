package com.ssambbong.gymjjak.trainer.routinerecommendation.application.service;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPtReservationPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out.TrainerRoutineAiPort;
import com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationErrorCode;
import com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrainerRoutineRecommendationSnapshotService {
    private static final int WORKOUT_HISTORY_DAYS = 28;
    private static final int RECENT_WORKOUT_LIMIT = 30;

    private final CalendarPtReservationPort calendarPtReservationPort;
    private final WorkoutDiaryPort workoutDiaryPort;

    // Reads and converts all DB data before the external AI request starts.
    @Transactional(readOnly = true)
    public TrainerRoutineAiPort.TrainerWorkoutData load(RecommendTrainerRoutineCommand command) {
        boolean allowed = calendarPtReservationPort.existsActivePtRelationWithTrainer(
                command.memberUserId(), command.trainerUserId());
        if (!allowed) {
            throw new TrainerRoutineRecommendationException(TrainerRoutineRecommendationErrorCode.MEMBER_ACCESS_DENIED);
        }

        LocalDate endDate = LocalDate.now().plusDays(1);
        List<TrainerRoutineAiPort.TrainerWorkoutSnapshot> workouts = workoutDiaryPort
                .findDiariesByUserIdAndPeriod(command.memberUserId(), endDate.minusDays(WORKOUT_HISTORY_DAYS), endDate)
                .stream()
                .map(TrainerRoutineRecommendationSnapshotService::toSnapshot)
                .toList();
        return summarizeWorkouts(workouts);
    }

    // Converts persistence-facing calendar results into the AI request snapshot.
    private static TrainerRoutineAiPort.TrainerWorkoutSnapshot toSnapshot(CalendarDayDiaryResult diary) {
        return new TrainerRoutineAiPort.TrainerWorkoutSnapshot(
                diary.date().toString(), diary.part().name(), diary.exercise(),
                diary.sets().stream().map(set -> new TrainerRoutineAiPort.TrainerWorkoutSetSnapshot(
                        set.setOrder(), set.weight(), set.reps())).toList());
    }

    // Retains recent details while aggregating the whole 28-day period for the AI prompt.
    private static TrainerRoutineAiPort.TrainerWorkoutData summarizeWorkouts(
            List<TrainerRoutineAiPort.TrainerWorkoutSnapshot> workouts
    ) {
        List<TrainerRoutineAiPort.TrainerWorkoutSnapshot> recentWorkouts = workouts.stream()
                .sorted(Comparator.comparing(TrainerRoutineAiPort.TrainerWorkoutSnapshot::diaryDate).reversed())
                .limit(RECENT_WORKOUT_LIMIT)
                .sorted(Comparator.comparing(TrainerRoutineAiPort.TrainerWorkoutSnapshot::diaryDate))
                .toList();
        Map<String, Integer> partSessionCounts = new LinkedHashMap<>();
        Map<String, BigDecimal> partTotalVolumeKg = new LinkedHashMap<>();
        for (TrainerRoutineAiPort.TrainerWorkoutSnapshot workout : workouts) {
            partSessionCounts.merge(workout.part(), 1, Integer::sum);
            BigDecimal volume = workout.sets().stream()
                    .map(set -> set.weight().multiply(BigDecimal.valueOf(set.reps())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            partTotalVolumeKg.merge(workout.part(), volume, BigDecimal::add);
        }
        int workoutDays = (int) workouts.stream().map(TrainerRoutineAiPort.TrainerWorkoutSnapshot::diaryDate).distinct().count();
        return new TrainerRoutineAiPort.TrainerWorkoutData(recentWorkouts,
                new TrainerRoutineAiPort.TrainerWorkoutSummary(WORKOUT_HISTORY_DAYS, workoutDays,
                        Map.copyOf(partSessionCounts), Map.copyOf(partTotalVolumeKg)));
    }
}
