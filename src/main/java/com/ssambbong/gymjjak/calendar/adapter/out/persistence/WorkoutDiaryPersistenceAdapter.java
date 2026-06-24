package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;
import com.ssambbong.gymjjak.category.infrastructure.persistence.CategoryJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WorkoutDiaryPersistenceAdapter implements WorkoutDiaryPort {

    private final WorkoutDiaryJpaRepository workoutDiaryJpaRepository;

    @Override
    public boolean existsByUserIdAndDiaryDate(
            Long userId,
            LocalDate diaryDate
    ) {
        return workoutDiaryJpaRepository.existsByUserIdAndDiaryDate(
                userId,
                diaryDate
        );
    }

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
    public void saveWorkoutDiary(WorkoutDiary workoutDiary) {
        WorkoutDiaryJpaEntity entity = new WorkoutDiaryJpaEntity(
                workoutDiary.getUserId(),
                workoutDiary.getCategoryId(),
                workoutDiary.getTitle(),
                workoutDiary.getContent(),
                workoutDiary.getDiaryDate()
        );

        workoutDiaryJpaRepository.save(entity);

    }

    @Override
    public void updateWorkoutDiary(
            Long userId,
            Long workoutDiaryId,
            Long categoryId,
            String title,
            String content
    ) {
        WorkoutDiaryJpaEntity workoutDiary = workoutDiaryJpaRepository.findByIdAndUserId(
                        workoutDiaryId,
                        userId
                )
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.DIARY_NOT_FOUND));

        workoutDiary.update(
                categoryId,
                title,
                content
        );
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
    public Optional<CalendarDayDiaryResult> findDiaryByUserIdAndDate(
            Long userId,
            LocalDate date
    ) {
        return workoutDiaryJpaRepository.findCalendarDayDiaryByUserIdAndDate(
                userId,
                date
        );
    }
}
