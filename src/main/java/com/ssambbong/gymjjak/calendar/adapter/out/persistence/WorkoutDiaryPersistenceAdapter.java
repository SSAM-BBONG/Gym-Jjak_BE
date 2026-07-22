package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiarySetResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthDiaryResult;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiarySet;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkoutDiaryPersistenceAdapter implements WorkoutDiaryPort {

    private final WorkoutDiaryJpaRepository workoutDiaryJpaRepository;
    private final WorkoutDiarySetJpaRepository workoutDiarySetJpaRepository;

    @Override
    public boolean existsByIdAndUserId(
            Long workoutDiaryId,
            Long userId
    ) {
        return workoutDiaryJpaRepository.existsByIdAndUserId(
                workoutDiaryId,
                userId
        );
    }

    @Override
    public Long saveWorkoutDiary(WorkoutDiary workoutDiary) {
        WorkoutDiaryJpaEntity entity = new WorkoutDiaryJpaEntity(
                workoutDiary.getUserId(),
                workoutDiary.getDiaryDate(),
                workoutDiary.getPart(),
                workoutDiary.getExerciseId(),
                workoutDiary.getExercise(),
                toSetEntities(workoutDiary.getSets())
        );

        WorkoutDiaryJpaEntity savedEntity = workoutDiaryJpaRepository.save(entity);

        return savedEntity.getId();
    }

    @Override
    public void updateWorkoutDiary(
            Long userId,
            Long workoutDiaryId,
            Long exerciseId,
            PartType part,
            String exercise,
            List<WorkoutDiarySet> sets
    ) {
        WorkoutDiaryJpaEntity workoutDiary = workoutDiaryJpaRepository.findByIdAndUserId(
                        workoutDiaryId,
                        userId
                )
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.DIARY_NOT_FOUND));

        workoutDiarySetJpaRepository.deleteByWorkoutDiaryId(workoutDiaryId);
        workoutDiarySetJpaRepository.flush();

        workoutDiary.update(part, exerciseId, exercise, toSetEntities(sets));
    }

    @Override
    public void deleteWorkoutDiary(
            Long userId,
            Long workoutDiaryId
    ) {
        WorkoutDiaryJpaEntity workoutDiary = workoutDiaryJpaRepository.findByIdAndUserId(
                        workoutDiaryId,
                        userId
                )
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.DIARY_NOT_FOUND));

        workoutDiaryJpaRepository.delete(workoutDiary);
    }

    @Override
    public List<CalendarDayDiaryResult> findDiariesByUserIdAndDate(
            Long userId,
            LocalDate date
    ) {
        return workoutDiaryJpaRepository.findAllWithSetsByUserIdAndDiaryDate(
                        userId,
                        date
                )
                .stream()
                .map(this::toCalendarDayDiaryResult)
                .toList();
    }

    @Override
    public List<CalendarMonthDiaryResult> findDiarySummariesByUserIdAndPeriod(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return workoutDiaryJpaRepository.findDiarySummariesByUserIdAndPeriod(
                userId,
                startDate,
                endDate
        );
    }

    @Override
    public LocalDate findDiaryDateByUserIdAndWorkoutDiaryId(
            Long userId,
            Long workoutDiaryId
    ) {
        return workoutDiaryJpaRepository.findDiaryDateByUserIdAndWorkoutDiaryId(
                userId,
                workoutDiaryId
        ).orElseThrow(() -> new CalendarException(CalendarErrorCode.DIARY_NOT_FOUND));
    }

    private List<WorkoutDiarySetJpaEntity> toSetEntities(List<WorkoutDiarySet> sets) {
        return sets.stream()
                .map(set -> new WorkoutDiarySetJpaEntity(
                        set.getSetOrder(),
                        set.getWeight(),
                        set.getReps()
                ))
                .toList();
    }

    private CalendarDayDiaryResult toCalendarDayDiaryResult(WorkoutDiaryJpaEntity entity) {
        return new CalendarDayDiaryResult(
                entity.getId(),
                entity.getExerciseId(),
                entity.getExercise(),
                entity.getDiaryDate(),
                entity.getPart(),
                entity.getSets()
                        .stream()
                        .sorted(Comparator.comparing(WorkoutDiarySetJpaEntity::getSetOrder))
                        .map(set -> new CalendarDayDiarySetResult(
                                set.getId(),
                                set.getSetOrder(),
                                set.getWeight(),
                                set.getReps()
                        ))
                        .toList()
        );
    }
}
