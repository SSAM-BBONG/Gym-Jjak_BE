package com.ssambbong.gymjjak.exercise.domain.model;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.time.LocalDateTime;

public class Exercise {

    private final Long id;
    private final PartType part;
    private final String exerciseName;
    private final LocalDateTime createdAt;

    private Exercise(
            Long id,
            PartType part,
            String exerciseName,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.part = part;
        this.exerciseName = exerciseName;
        this.createdAt = createdAt;
    }

    public static Exercise create(
            PartType part,
            String exerciseName
    ) {
        return new Exercise(null, part, exerciseName, null);
    }

    public static Exercise reconstruct(
            Long id,
            PartType part,
            String exerciseName,
            LocalDateTime createdAt
    ) {
        return new Exercise(id, part, exerciseName, createdAt);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
