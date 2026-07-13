package com.ssambbong.gymjjak.exercise.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarExercisePort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarExerciseSnapshot;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseErrorCode;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarExercisePersistenceAdapter implements CalendarExercisePort {

    private final ExerciseJpaRepository exerciseJpaRepository;

    @Override
    @Cacheable(
            cacheNames = "exerciseSnapshot",
            key = "T(com.ssambbong.gymjjak.global.infrastructure.cache.ExerciseCacheKeys).snapshot(#exerciseId, #part)",
            sync = true
    )
    public CalendarExerciseSnapshot findExerciseByIdAndPart(
            Long exerciseId,
            PartType part
    ) {
        ExerciseJpaEntity exercise = exerciseJpaRepository.findByIdAndPart(exerciseId, part)
                .orElseThrow(() -> new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_FOUND));

        return new CalendarExerciseSnapshot(
                exercise.getId(),
                exercise.getPart(),
                exercise.getExerciseName()
        );
    }
}
