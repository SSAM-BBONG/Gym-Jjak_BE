package com.ssambbong.gymjjak.exercise.application.service;

import com.ssambbong.gymjjak.exercise.application.command.CreateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.command.UpdateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.port.in.ExerciseUseCase;
import com.ssambbong.gymjjak.exercise.application.port.out.ExerciseCacheEvictionPort;
import com.ssambbong.gymjjak.exercise.application.port.out.ExercisePort;
import com.ssambbong.gymjjak.exercise.application.result.ExerciseResult;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseErrorCode;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseException;
import com.ssambbong.gymjjak.exercise.domain.model.Exercise;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService implements ExerciseUseCase {

    private final ExercisePort exercisePort;
    private final ExerciseCacheEvictionPort exerciseCacheEvictionPort;

    @Override
    public Long createExercise(CreateExerciseCommand command) {
        String exerciseName = normalize(command.exerciseName());
        validateDuplicate(command.part(), exerciseName);

        Long exerciseId = exercisePort.saveExercise(
                Exercise.create(
                        command.part(),
                        exerciseName
                )
        );
        evictAfterCommit(exerciseCacheEvictionPort::evictExerciseList);
        return exerciseId;
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
        evictAfterCommit(() -> {
            exerciseCacheEvictionPort.evictExerciseList();
            exerciseCacheEvictionPort.evictExerciseSnapshots();
        });
    }

    @Override
    public void deleteExercise(Long exerciseId) {
        Exercise exercise = exercisePort.findExerciseById(exerciseId)
                .orElseThrow(() -> new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_FOUND));

        exercisePort.deleteExercise(exercise);
        evictAfterCommit(() -> {
            exerciseCacheEvictionPort.evictExerciseList();
            exerciseCacheEvictionPort.evictExerciseSnapshots();
        });
    }

    @Override
    @Cacheable(
            cacheNames = "exerciseList",
            key = "T(com.ssambbong.gymjjak.global.infrastructure.cache.ExerciseCacheKeys).list(#part, #keyword)",
            sync = true
    )
    @Transactional(readOnly = true)
    public List<ExerciseResult> findExercises(PartType part, String keyword) {
        List<Exercise> exercises;

        if (part == null && isBlank(keyword)) {
            exercises = exercisePort.findAllExercises();
        } else if (part == null) {
            exercises = exercisePort.findExercisesByKeyword(keyword.trim());
        } else if (isBlank(keyword)) {
            exercises = exercisePort.findExercisesByPart(part);
        } else {
            exercises = exercisePort.findExercisesByPartAndKeyword(
                    part,
                    keyword.trim()
            );
        }

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
                exercise.getExerciseName(),
                exercise.getCreatedAt()
        );
    }

    private String normalize(String value) {
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void evictAfterCommit(Runnable eviction) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            eviction.run();
                        }
                    }
            );
            return;
        }
        eviction.run();
    }
}
