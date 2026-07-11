package com.ssambbong.gymjjak.exercise.adapter.out.persistence;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarExercisePort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarExerciseSnapshot;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarExercisePersistenceAdapter implements CalendarExercisePort {

    private final ExerciseJpaRepository exerciseJpaRepository;

    @Override
    public CalendarExerciseSnapshot findExerciseByIdAndPart(
            Long exerciseId,
            PartType part
    ) {
        ExerciseJpaEntity exercise = exerciseJpaRepository.findByIdAndPart(exerciseId, part)
                .orElseThrow(() -> new CalendarException(CalendarErrorCode.EXERCISE_NOT_FOUND));

        return new CalendarExerciseSnapshot(
                exercise.getId(),
                exercise.getPart(),
                exercise.getExerciseName()
        );
    }
}
