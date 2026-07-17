package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "meal_analysis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealAnalysisJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 20)
    private MealType mealType;

    @Column(name = "meal_time", nullable = false)
    private LocalDateTime mealTime;

    @Column(name = "menu", nullable = false, length = 255)
    private String menu;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "kcal")
    private Long kcal;

    private MealAnalysisJpaEntity(MealAnalysis meal) {
        this.id = meal.getId();
        this.userId = meal.getUserId();
        update(meal);
    }

    public static MealAnalysisJpaEntity from(MealAnalysis meal) {
        return new MealAnalysisJpaEntity(meal);
    }

    public void update(MealAnalysis meal) {
        this.mealType = meal.getMealType();
        this.mealTime = meal.getMealTime();
        this.menu = meal.getMenu();
        this.fileId = meal.getFileId();
        this.kcal = meal.getKcal();
    }

    public MealAnalysis toDomain() {
        return MealAnalysis.builder()
                .id(id).userId(userId).mealType(mealType).mealTime(mealTime).menu(menu)
                .fileId(fileId).kcal(kcal).createdAt(getCreatedAt()).updatedAt(getUpdatedAt())
                .build();
    }
}
