package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.CreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "onboarding_surveys")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingSurveyJpaEntity extends CreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "onboarding_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "exercise_goal", nullable = false, length = 100)
    private String exerciseGoal;

    @Column(name = "exercise_period", nullable = false, length = 100)
    private String exercisePeriod;

    @Column(name = "exercise_frequency", nullable = false, length = 100)
    private String exerciseFrequency;

    @Column(name = "preferred_exercise", nullable = false, length = 100)
    private String preferredExercise;

    @Column(name = "preferred_region_id", nullable = false)
    private Long preferredRegionId;

    @Column(name = "height", nullable = false, precision = 5, scale = 2)
    private BigDecimal height;

    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    public OnboardingSurveyJpaEntity(
            Long id,
            Long userId,
            String exerciseGoal,
            String exercisePeriod,
            String exerciseFrequency,
            String preferredExercise,
            Long preferredRegionId,
            BigDecimal height,
            BigDecimal weight
    ) {
        this.id = id;
        this.userId = userId;
        this.exerciseGoal = exerciseGoal;
        this.exercisePeriod = exercisePeriod;
        this.exerciseFrequency = exerciseFrequency;
        this.preferredExercise = preferredExercise;
        this.preferredRegionId = preferredRegionId;
        this.height = height;
        this.weight = weight;
    }
}
