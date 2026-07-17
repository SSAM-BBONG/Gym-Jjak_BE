package com.ssambbong.gymjjak.diet.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MealAnalysis {

    private final Long id;
    private final Long userId;
    private MealType mealType;
    private LocalDateTime mealTime;
    private String menu;
    private Long kcal;
    private Long fileId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PUBLIC)
    private MealAnalysis(Long id, Long userId, MealType mealType, LocalDateTime mealTime,
                         String menu, Long kcal, Long fileId,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.mealType = mealType;
        this.mealTime = mealTime;
        this.menu = menu;
        this.kcal = kcal;
        this.fileId = fileId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MealAnalysis create(Long userId, MealType mealType, LocalDateTime mealTime,
                                      String menu, Long kcal, Long fileId) {
        return MealAnalysis.builder()
                .userId(userId)
                .mealType(mealType)
                .mealTime(mealTime)
                .menu(menu)
                .kcal(kcal)
                .fileId(fileId)
                .build();
    }

    // 식단 수정 규칙을 도메인 안에서 일관되게 관리한다.
    public void update(MealType mealType, LocalDateTime mealTime, String menu, Long kcal, Long fileId) {
        this.mealType = mealType;
        this.mealTime = mealTime;
        this.menu = menu;
        this.kcal = kcal;
        this.fileId = fileId;
    }
}
