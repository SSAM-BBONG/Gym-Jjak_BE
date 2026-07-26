package com.ssambbong.gymjjak.trainer.routinerecommendation.application.service;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPtReservationPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.in.TrainerRoutineRecommendationUseCase;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out.TrainerRoutineAiPort;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;
import com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationErrorCode;
import com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerRoutineRecommendationService implements TrainerRoutineRecommendationUseCase {
    private static final int WORKOUT_HISTORY_DAYS = 28;

    private final CalendarPtReservationPort calendarPtReservationPort;
    private final WorkoutDiaryPort workoutDiaryPort;
    private final TrainerRoutineAiPort trainerRoutineAiPort;

    @Override
    @Transactional(readOnly = true)
    public TrainerRoutineRecommendationResult recommend(RecommendTrainerRoutineCommand command) {
        boolean allowed = calendarPtReservationPort.existsActivePtRelationWithTrainer(
                command.memberUserId(), command.trainerUserId());
        if (!allowed) {
            throw new TrainerRoutineRecommendationException(TrainerRoutineRecommendationErrorCode.MEMBER_ACCESS_DENIED);
        }

        LocalDate endDate = LocalDate.now().plusDays(1);
        List<TrainerRoutineAiPort.TrainerWorkoutSnapshot> workouts = workoutDiaryPort
                .findDiariesByUserIdAndPeriod(command.memberUserId(), endDate.minusDays(WORKOUT_HISTORY_DAYS), endDate)
                .stream()
                .map(TrainerRoutineRecommendationService::toSnapshot)
                .toList();

        return trainerRoutineAiPort.recommend(command, workouts);
    }

    private static TrainerRoutineAiPort.TrainerWorkoutSnapshot toSnapshot(CalendarDayDiaryResult diary) {
        return new TrainerRoutineAiPort.TrainerWorkoutSnapshot(
                diary.date().toString(), diary.part().name(), diary.exercise(),
                diary.sets().stream()
                        .map(set -> new TrainerRoutineAiPort.TrainerWorkoutSetSnapshot(
                                set.setOrder(), set.weight(), set.reps()))
                        .toList()
        );
    }
}
