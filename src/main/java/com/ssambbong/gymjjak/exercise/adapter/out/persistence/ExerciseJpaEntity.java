package com.ssambbong.gymjjak.exercise.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "exercises",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exercises_part_name",
                        columnNames = {"part", "exercise_name"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false, length = 30)
    private PartType part;

    @Column(name = "exercise_name", nullable = false, length = 100)
    private String exerciseName;

    public ExerciseJpaEntity(
            PartType part,
            String exerciseName
    ) {
        this.part = part;
        this.exerciseName = exerciseName;
    }

    public void update(
            PartType part,
            String exerciseName
    ) {
        this.part = part;
        this.exerciseName = exerciseName;
    }

    public void updateExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}
