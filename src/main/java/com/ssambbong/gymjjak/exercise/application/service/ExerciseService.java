package com.ssambbong.gymjjak.exercise.application.service;

import com.ssambbong.gymjjak.exercise.adapter.out.persistence.ExerciseJpaEntity;
import com.ssambbong.gymjjak.exercise.adapter.out.persistence.ExerciseJpaRepository;
import com.ssambbong.gymjjak.exercise.application.command.CreateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.command.UpdateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.port.in.ExerciseUseCase;
import com.ssambbong.gymjjak.exercise.application.result.ExerciseResult;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseErrorCode;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService implements ExerciseUseCase {

    private final ExerciseJpaRepository exerciseJpaRepository;

    @Override
    public Long createExercise(CreateExerciseCommand command) {
        String exerciseName = normalize(command.exerciseName());
        validateDuplicate(command.part(), exerciseName);

        try {
            ExerciseJpaEntity saved = exerciseJpaRepository.save(
                    new ExerciseJpaEntity(command.part(), exerciseName)
            );
            return saved.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }
    }

    @Override
    public void updateExercise(Long exerciseId, UpdateExerciseCommand command) {
        ExerciseJpaEntity exercise = exerciseJpaRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_FOUND));

        String exerciseName = normalize(command.exerciseName());
        if (exerciseJpaRepository.existsByPartAndExerciseNameAndIdNot(
                exercise.getPart(),
                exerciseName,
                exerciseId
        )) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }

        try {
            exercise.updateExerciseName(exerciseName);
        } catch (DataIntegrityViolationException ex) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }
    }

    @Override
    public void deleteExercise(Long exerciseId) {
        ExerciseJpaEntity exercise = exerciseJpaRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_FOUND));

        exerciseJpaRepository.delete(exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResult> findExercises(PartType part, String keyword) {
        List<ExerciseJpaEntity> exercises = isBlank(keyword)
                ? exerciseJpaRepository.findByPartOrderByExerciseNameAsc(part)
                : exerciseJpaRepository.findByPartAndExerciseNameContainingIgnoreCaseOrderByExerciseNameAsc(
                        part,
                        keyword.trim()
                );

        return exercises.stream()
                .map(this::toResult)
                .toList();
    }

    private void validateDuplicate(PartType part, String exerciseName) {
        if (exerciseJpaRepository.existsByPartAndExerciseName(part, exerciseName)) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }
    }

    private ExerciseResult toResult(ExerciseJpaEntity entity) {
        return new ExerciseResult(
                entity.getId(),
                entity.getPart(),
                entity.getExerciseName()
        );
    }

    private String normalize(String value) {
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
