package com.ssambbong.gymjjak.diet.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
public class MealAnalysis {

    private final Long id;
    private final Long userId;
    private MealType mealType;
    private LocalDateTime mealTime;
    private String menu;
    private Long kcal;
    // AI 분석으로 산출된 영양성분이며 일반 식단은 값이 없을 수 있다.
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private Long fileId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PUBLIC)
    private MealAnalysis(Long id, Long userId, MealType mealType, LocalDateTime mealTime,
                         String menu, Long kcal, BigDecimal carbohydrate, BigDecimal protein, BigDecimal fat, Long fileId,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.mealType = mealType;
        this.mealTime = mealTime;
        this.menu = menu;
        this.kcal = kcal;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.fileId = fileId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MealAnalysis create(Long userId, MealType mealType, LocalDateTime mealTime,
                                      String menu, Long kcal, BigDecimal carbohydrate,
                                      BigDecimal protein, BigDecimal fat, Long fileId) {
        return MealAnalysis.builder()
                .userId(userId)
                .mealType(mealType)
                .mealTime(mealTime)
                .menu(menu)
                .kcal(kcal)
                .carbohydrate(carbohydrate)
                .protein(protein)
                .fat(fat)
                .fileId(fileId)
                .build();
    }

    // 요청에 포함된 값만 변경하고, 선택 필드는 명시적인 null로 제거할 수 있다.
    public void update(
            MealType mealType, boolean mealTypePresent,
            LocalDateTime mealTime, boolean mealTimePresent,
            String menu, boolean menuPresent,
            Long kcal, boolean kcalPresent,
            BigDecimal carbohydrate, boolean carbohydratePresent,
            BigDecimal protein, boolean proteinPresent,
            BigDecimal fat, boolean fatPresent,
            Long fileId, boolean fileIdPresent
    ) {
        if (mealTypePresent) {
            this.mealType = mealType;
        }
        if (mealTimePresent) {
            this.mealTime = mealTime;
        }
        if (menuPresent) {
            this.menu = menu;
        }
        if (kcalPresent) {
            this.kcal = kcal;
        }
        // PATCH 요청에 필드가 포함된 경우에만 변경한다. 명시적인 null은 기존 값을 제거한다.
        if (carbohydratePresent) {
            this.carbohydrate = carbohydrate;
        }
        if (proteinPresent) {
            this.protein = protein;
        }
        if (fatPresent) {
            this.fat = fat;
        }
        if (fileIdPresent) {
            this.fileId = fileId;
        }
    }
}
