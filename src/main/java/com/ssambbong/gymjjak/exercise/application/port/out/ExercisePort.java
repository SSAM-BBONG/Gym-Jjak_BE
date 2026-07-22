package com.ssambbong.gymjjak.exercise.application.port.out;

import com.ssambbong.gymjjak.exercise.domain.model.Exercise;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.util.List;
import java.util.Optional;

public interface ExercisePort {

    Long saveExercise(Exercise exercise);

    Optional<Exercise> findExerciseById(Long exerciseId);

    void updateExerciseName(
            Long exerciseId,
            String exerciseName
    );

    void deleteExercise(Exercise exercise);

    boolean existsByPartAndExerciseName(
            PartType part,
            String exerciseName
    );

    boolean existsByPartAndExerciseNameAndIdNot(
            PartType part,
            String exerciseName,
            Long exerciseId
    );

    List<Exercise> findAllExercises();

    List<Exercise> findExercisesByKeyword(String keyword);

    List<Exercise> findExercisesByPart(PartType part);

    List<Exercise> findExercisesByPartAndKeyword(
            PartType part,
            String keyword
    );
}
