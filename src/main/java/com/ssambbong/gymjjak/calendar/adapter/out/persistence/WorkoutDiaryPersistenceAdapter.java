package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.calendar.domain.model.WorkoutDiary;
import com.ssambbong.gymjjak.category.infrastructure.persistence.CategoryJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
}
