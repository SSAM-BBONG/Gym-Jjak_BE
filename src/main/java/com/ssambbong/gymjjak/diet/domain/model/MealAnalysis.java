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

    // 요청에 포함된 값만 변경하고, 선택 필드는 명시적인 null로 제거할 수 있다.
    public void update(
            MealType mealType, boolean mealTypePresent,
            LocalDateTime mealTime, boolean mealTimePresent,
            String menu, boolean menuPresent,
            Long kcal, boolean kcalPresent,
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
        if (fileIdPresent) {
            this.fileId = fileId;
        }
    }
}
