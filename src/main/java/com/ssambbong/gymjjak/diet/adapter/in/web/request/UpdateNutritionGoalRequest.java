package com.ssambbong.gymjjak.diet.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "일일 영양 목표 부분 수정 요청")
public class UpdateNutritionGoalRequest {
    @PositiveOrZero(message = "목표 단백질은 0 이상이어야 합니다.")
    @Schema(description = "변경할 일일 목표 단백질(g). 0 이상이며 상한은 없습니다.", example = "130", minimum = "0", nullable = true)
    private Long goalProtein;
    @PositiveOrZero(message = "목표 탄수화물은 0 이상이어야 합니다.")
    @Schema(description = "변경할 일일 목표 탄수화물(g). 0 이상이며 상한은 없습니다.", example = "280", minimum = "0", nullable = true)
    private Long goalCarbohydrate;
    @PositiveOrZero(message = "목표 지방은 0 이상이어야 합니다.")
    @Schema(description = "변경할 일일 목표 지방(g). 0 이상이며 상한은 없습니다.", example = "65", minimum = "0", nullable = true)
    private Long goalFat;
    @PositiveOrZero(message = "목표 칼로리는 0 이상이어야 합니다.")
    @Schema(description = "변경할 일일 목표 칼로리(kcal). 0 이상이며 상한은 없습니다.", example = "2200", minimum = "0", nullable = true)
    private Long dailyGoalKcal;
    @JsonIgnore private boolean proteinPresent;
    @JsonIgnore private boolean carbohydratePresent;
    @JsonIgnore private boolean fatPresent;
    @JsonIgnore private boolean kcalPresent;

    @JsonSetter("goalProtein") public void setGoalProtein(Long value) { proteinPresent = true; goalProtein = value; }
    @JsonSetter("goalCarbohydrate") public void setGoalCarbohydrate(Long value) { carbohydratePresent = true; goalCarbohydrate = value; }
    @JsonSetter("goalFat") public void setGoalFat(Long value) { fatPresent = true; goalFat = value; }
    @JsonSetter("dailyGoalKcal") public void setDailyGoalKcal(Long value) { kcalPresent = true; dailyGoalKcal = value; }
    public boolean hasAnyField() { return proteinPresent || carbohydratePresent || fatPresent || kcalPresent; }
}
