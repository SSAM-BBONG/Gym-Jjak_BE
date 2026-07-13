package com.ssambbong.gymjjak.exercise.application.service;

import com.ssambbong.gymjjak.exercise.application.command.CreateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.command.UpdateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.port.in.ExerciseUseCase;
import com.ssambbong.gymjjak.exercise.application.port.out.ExercisePort;
import com.ssambbong.gymjjak.exercise.application.result.ExerciseResult;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseErrorCode;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseException;
import com.ssambbong.gymjjak.exercise.domain.model.Exercise;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService implements ExerciseUseCase {

    private final ExercisePort exercisePort;

    @Override
    public Long createExercise(CreateExerciseCommand command) {
        String exerciseName = normalize(command.exerciseName());
        validateDuplicate(command.part(), exerciseName);

        return exercisePort.saveExercise(
                Exercise.create(
                        command.part(),
                        exerciseName
                )
        );
    }

    @Override
    public void updateExercise(Long exerciseId, UpdateExerciseCommand command) {
        Exercise exercise = exercisePort.findExerciseById(exerciseId)
                .orElseThrow(() -> new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_FOUND));

        String exerciseName = normalize(command.exerciseName());
        if (exercisePort.existsByPartAndExerciseNameAndIdNot(
                exercise.getPart(),
                exerciseName,
                exerciseId
        )) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }

        exercisePort.updateExerciseName(exerciseId, exerciseName);
    }

    @Override
    public void deleteExercise(Long exerciseId) {
        Exercise exercise = exercisePort.findExerciseById(exerciseId)
                .orElseThrow(() -> new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_FOUND));

        exercisePort.deleteExercise(exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResult> findExercises(PartType part, String keyword) {
        List<Exercise> exercises = isBlank(keyword)
                ? exercisePort.findExercisesByPart(part)
                : exercisePort.findExercisesByPartAndKeyword(
                        part,
                        keyword.trim()
                );

        return exercises.stream()
                .map(this::toResult)
                .toList();
    }

    private void validateDuplicate(PartType part, String exerciseName) {
        if (exercisePort.existsByPartAndExerciseName(part, exerciseName)) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }
    }

    private ExerciseResult toResult(Exercise exercise) {
        return new ExerciseResult(
                exercise.getId(),
                exercise.getPart(),
                exercise.getExerciseName()
        );
    }

    private String normalize(String value) {
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
