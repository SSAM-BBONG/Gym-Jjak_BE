package com.ssambbong.gymjjak.exercise.application.port.in;

import com.ssambbong.gymjjak.exercise.application.command.CreateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.command.UpdateExerciseCommand;
import com.ssambbong.gymjjak.exercise.application.result.ExerciseResult;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.util.List;

public interface ExerciseUseCase {

    Long createExercise(CreateExerciseCommand command);

    void updateExercise(Long exerciseId, UpdateExerciseCommand command);

    void deleteExercise(Long exerciseId);

    List<ExerciseResult> findExercises(PartType part, String keyword);
}
