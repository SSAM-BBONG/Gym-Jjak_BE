package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "nutrition_goals", uniqueConstraints = @UniqueConstraint(name = "uk_nutrition_goals_user_id", columnNames = "user_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NutritionGoalJpaEntity extends BaseCreatedUpdatedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "goal_pro", nullable = false)
    private Long goalProtein;
    @Column(name = "goal_car", nullable = false)
    private Long goalCarbohydrate;
    @Column(name = "goal_fat", nullable = false)
    private Long goalFat;
    @Column(name = "daily_goal_kcal", nullable = false)
    private Long dailyGoalKcal;

    private NutritionGoalJpaEntity(NutritionGoal goal) {
        this.id = goal.getId();
        this.userId = goal.getUserId();
        update(goal);
    }

    public static NutritionGoalJpaEntity from(NutritionGoal goal) { return new NutritionGoalJpaEntity(goal); }

    public void update(NutritionGoal goal) {
        this.goalProtein = goal.getGoalProtein();
        this.goalCarbohydrate = goal.getGoalCarbohydrate();
        this.goalFat = goal.getGoalFat();
        this.dailyGoalKcal = goal.getDailyGoalKcal();
    }

    public NutritionGoal toDomain() {
        return NutritionGoal.builder().id(id).userId(userId).goalProtein(goalProtein)
                .goalCarbohydrate(goalCarbohydrate).goalFat(goalFat).dailyGoalKcal(dailyGoalKcal)
                .createdAt(getCreatedAt()).updatedAt(getUpdatedAt()).build();
    }
}
