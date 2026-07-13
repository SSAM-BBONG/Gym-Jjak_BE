package com.ssambbong.gymjjak.exercise.adapter.out.persistence;

import com.ssambbong.gymjjak.exercise.application.port.out.ExercisePort;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseErrorCode;
import com.ssambbong.gymjjak.exercise.domain.exception.ExerciseException;
import com.ssambbong.gymjjak.exercise.domain.model.Exercise;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExercisePersistenceAdapter implements ExercisePort {

    private final ExerciseJpaRepository exerciseJpaRepository;

    @Override
    public Long saveExercise(Exercise exercise) {
        try {
            ExerciseJpaEntity saved = exerciseJpaRepository.saveAndFlush(
                    new ExerciseJpaEntity(
                            exercise.getPart(),
                            exercise.getExerciseName()
                    )
            );
            return saved.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }
    }

    @Override
    public Optional<Exercise> findExerciseById(Long exerciseId) {
        return exerciseJpaRepository.findById(exerciseId)
                .map(this::toDomain);
    }

    @Override
    public void updateExerciseName(
            Long exerciseId,
            String exerciseName
    ) {
        ExerciseJpaEntity exercise = exerciseJpaRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_FOUND));

        try {
            exercise.updateExerciseName(exerciseName);
            exerciseJpaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ExerciseException(ExerciseErrorCode.EXERCISE_DUPLICATED);
        }
    }

    @Override
    public void deleteExercise(Exercise exercise) {
        exerciseJpaRepository.deleteById(exercise.getId());
    }

    @Override
    public boolean existsByPartAndExerciseName(
            PartType part,
            String exerciseName
    ) {
        return exerciseJpaRepository.existsByPartAndExerciseName(part, exerciseName);
    }

    @Override
    public boolean existsByPartAndExerciseNameAndIdNot(
            PartType part,
            String exerciseName,
            Long exerciseId
    ) {
        return exerciseJpaRepository.existsByPartAndExerciseNameAndIdNot(
                part,
                exerciseName,
                exerciseId
        );
    }

    @Override
    public List<Exercise> findExercisesByPart(PartType part) {
        return exerciseJpaRepository.findByPartOrderByExerciseNameAsc(part)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Exercise> findExercisesByPartAndKeyword(
            PartType part,
            String keyword
    ) {
        return exerciseJpaRepository.findByPartAndExerciseNameContainingIgnoreCaseOrderByExerciseNameAsc(
                        part,
                        keyword
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Exercise toDomain(ExerciseJpaEntity entity) {
        return Exercise.reconstruct(
                entity.getId(),
                entity.getPart(),
                entity.getExerciseName()
        );
    }
}
