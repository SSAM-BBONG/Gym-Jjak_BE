package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(
        name = "workout_diary_sets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workout_diary_sets_diary_order",
                        columnNames = {"workout_diary_id", "set_order"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkoutDiarySetJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_diary_id", nullable = false)
    private WorkoutDiaryJpaEntity workoutDiary;

    @Column(name = "set_order", nullable = false)
    private Integer setOrder;

    @Column(name = "weight", nullable = false, precision = 6, scale = 2)
    private BigDecimal weight;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    public WorkoutDiarySetJpaEntity(
            Integer setOrder,
            BigDecimal weight,
            Integer reps
    ) {
        this.setOrder = setOrder;
        this.weight = weight;
        this.reps = reps;
    }

    void assignTo(WorkoutDiaryJpaEntity workoutDiary) {
        this.workoutDiary = workoutDiary;
    }
}
