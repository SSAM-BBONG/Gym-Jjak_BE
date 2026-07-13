package com.ssambbong.gymjjak.exercise.domain.model;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

public class Exercise {

    private final Long id;
    private final PartType part;
    private final String exerciseName;

    private Exercise(
            Long id,
            PartType part,
            String exerciseName
    ) {
        this.id = id;
        this.part = part;
        this.exerciseName = exerciseName;
    }

    public static Exercise create(
            PartType part,
            String exerciseName
    ) {
        return new Exercise(null, part, exerciseName);
    }

    public static Exercise reconstruct(
            Long id,
            PartType part,
            String exerciseName
    ) {
        return new Exercise(id, part, exerciseName);
    }

    public Long getId() {
        return id;
    }

    public PartType getPart() {
        return part;
    }

    public String getExerciseName() {
        return exerciseName;
    }
}
