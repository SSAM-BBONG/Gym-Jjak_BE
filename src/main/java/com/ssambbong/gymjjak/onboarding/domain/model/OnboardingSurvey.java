package com.ssambbong.gymjjak.onboarding.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OnboardingSurvey {

    private final Long id;
    private final Long userId;
    private final String exerciseGoal;
    private final String exercisePeriod;
    private final String exerciseFrequency;
    private final String preferredExercise;
    private final Long preferredRegionId;
    private final BigDecimal height;
    private final BigDecimal weight;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private OnboardingSurvey(
            Long id,
            Long userId,
            String exerciseGoal,
            String exercisePeriod,
            String exerciseFrequency,
            String preferredExercise,
            Long preferredRegionId,
            BigDecimal height,
            BigDecimal weight,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");
        this.exerciseGoal = Objects.requireNonNull(exerciseGoal, "운동 목적은 필수입니다.");
        this.exercisePeriod = Objects.requireNonNull(exercisePeriod, "운동 기간은 필수입니다.");
        this.exerciseFrequency = Objects.requireNonNull(exerciseFrequency, "운동 빈도는 필수입니다.");
        this.preferredExercise = Objects.requireNonNull(preferredExercise, "선호 운동은 필수입니다.");
        this.preferredRegionId = Objects.requireNonNull(preferredRegionId, "선호 지역 ID는 필수입니다.");
        this.height = Objects.requireNonNull(height, "키는 필수입니다.");
        this.weight = Objects.requireNonNull(weight, "몸무게는 필수입니다.");
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OnboardingSurvey create(
            Long userId,
            String exerciseGoal,
            String exercisePeriod,
            String exerciseFrequency,
            String preferredExercise,
            Long preferredRegionId,
            BigDecimal height,
            BigDecimal weight
    ) {
        return new OnboardingSurvey(
                null,
                userId,
                exerciseGoal,
                exercisePeriod,
                exerciseFrequency,
                preferredExercise,
                preferredRegionId,
                height,
                weight,
                null,
                null
        );
    }

    public static OnboardingSurvey reconstruct(
            Long id,
            Long userId,
            String exerciseGoal,
            String exercisePeriod,
            String exerciseFrequency,
            String preferredExercise,
            Long preferredRegionId,
            BigDecimal height,
            BigDecimal weight,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new OnboardingSurvey(
                id,
                userId,
                exerciseGoal,
                exercisePeriod,
                exerciseFrequency,
                preferredExercise,
                preferredRegionId,
                height,
                weight,
                createdAt,
                updatedAt
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getExerciseGoal() {
        return exerciseGoal;
    }

    public String getExercisePeriod() {
        return exercisePeriod;
    }

    public String getExerciseFrequency() {
        return exerciseFrequency;
    }

    public String getPreferredExercise() {
        return preferredExercise;
    }

    public Long getPreferredRegionId() {
        return preferredRegionId;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}

