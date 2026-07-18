package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

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

    // migration의 DECIMAL(8, 2) 정의와 동일한 정밀도로 영양성분을 저장한다.
    @Column(name = "carbohydrate_g", precision = 8, scale = 2)
    private BigDecimal carbohydrate;

    @Column(name = "protein_g", precision = 8, scale = 2)
    private BigDecimal protein;

    @Column(name = "fat_g", precision = 8, scale = 2)
    private BigDecimal fat;

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
        this.carbohydrate = meal.getCarbohydrate();
        this.protein = meal.getProtein();
        this.fat = meal.getFat();
    }

    public MealAnalysis toDomain() {
        return MealAnalysis.builder()
                .id(id).userId(userId).mealType(mealType).mealTime(mealTime).menu(menu)
                .fileId(fileId).kcal(kcal).carbohydrate(carbohydrate).protein(protein).fat(fat)
                .createdAt(getCreatedAt()).updatedAt(getUpdatedAt())
                .build();
    }
}
