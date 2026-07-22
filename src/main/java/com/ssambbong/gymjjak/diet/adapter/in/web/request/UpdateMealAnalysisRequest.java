package com.ssambbong.gymjjak.diet.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Schema(description = "식단 부분 수정 요청. 전달한 필드만 변경됩니다.")
public class UpdateMealAnalysisRequest {

    @Schema(description = "변경할 식사 유형", example = "점심",
            allowableValues = {"아침", "점심", "저녁", "간식"}, nullable = true)
    private String mealType;

    @Schema(description = "변경할 식사 일시", example = "2026-07-18 12:30", type = "string", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime mealTime;

    @Schema(description = "변경할 메뉴", example = "닭가슴살 샐러드", nullable = true)
    @Size(max = 255, message = "메뉴는 255자 이하여야 합니다.")
    private String menu;

    @Schema(description = "변경할 섭취 열량. null을 명시하면 기존 값을 제거합니다.", example = "350", nullable = true)
    @PositiveOrZero(message = "kcal은 0 이상이어야 합니다.")
    private Long kcal;

    @Schema(description = "변경할 탄수화물(g). null로 제거 가능하며 AI 구독 사용자만 수정 가능", example = "67.00", nullable = true)
    @PositiveOrZero(message = "탄수화물은 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 2, message = "탄수화물은 소수점 둘째 자리까지 입력할 수 있습니다.")
    private BigDecimal carbohydrate;

    @Schema(description = "변경할 단백질(g). null로 제거 가능하며 AI 구독 사용자만 수정 가능", example = "51.90", nullable = true)
    @PositiveOrZero(message = "단백질은 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 2, message = "단백질은 소수점 둘째 자리까지 입력할 수 있습니다.")
    private BigDecimal protein;

    @Schema(description = "변경할 지방(g). null로 제거 가능하며 AI 구독 사용자만 수정 가능", example = "7.60", nullable = true)
    @PositiveOrZero(message = "지방은 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 2, message = "지방은 소수점 둘째 자리까지 입력할 수 있습니다.")
    private BigDecimal fat;

    @Schema(description = "변경할 사진 파일 ID. null을 명시하면 기존 값을 제거합니다.", example = "15", nullable = true)
    @PositiveOrZero(message = "파일 ID는 0 이상이어야 합니다.")
    private Long fileId;

    @JsonIgnore
    @Schema(hidden = true)
    private boolean mealTypePresent;
    @JsonIgnore
    @Schema(hidden = true)
    private boolean mealTimePresent;
    @JsonIgnore
    @Schema(hidden = true)
    private boolean menuPresent;
    @JsonIgnore
    @Schema(hidden = true)
    private boolean kcalPresent;
    @JsonIgnore
    @Schema(hidden = true)
    private boolean carbohydratePresent;
    @JsonIgnore
    @Schema(hidden = true)
    private boolean proteinPresent;
    @JsonIgnore
    @Schema(hidden = true)
    private boolean fatPresent;
    @JsonIgnore
    @Schema(hidden = true)
    private boolean fileIdPresent;

    @JsonSetter("mealType")
    public void setMealType(String mealType) {
        this.mealTypePresent = true;
        this.mealType = mealType;
    }

    @JsonSetter("mealTime")
    public void setMealTime(LocalDateTime mealTime) {
        this.mealTimePresent = true;
        this.mealTime = mealTime;
    }

    @JsonSetter("menu")
    public void setMenu(String menu) {
        this.menuPresent = true;
        this.menu = menu;
    }

    @JsonSetter("kcal")
    public void setKcal(Long kcal) {
        this.kcalPresent = true;
        this.kcal = kcal;
    }

    @JsonSetter("carbohydrate")
    public void setCarbohydrate(BigDecimal carbohydrate) {
        // 필드 누락과 명시적 null을 구분해 부분 수정 및 값 제거를 지원한다.
        this.carbohydratePresent = true;
        this.carbohydrate = carbohydrate;
    }

    @JsonSetter("protein")
    public void setProtein(BigDecimal protein) {
        this.proteinPresent = true;
        this.protein = protein;
    }

    @JsonSetter("fat")
    public void setFat(BigDecimal fat) {
        this.fatPresent = true;
        this.fat = fat;
    }

    @JsonSetter("fileId")
    public void setFileId(Long fileId) {
        this.fileIdPresent = true;
        this.fileId = fileId;
    }

    public boolean hasAnyField() {
        return mealTypePresent || mealTimePresent || menuPresent || kcalPresent
                || carbohydratePresent || proteinPresent || fatPresent || fileIdPresent;
    }
}
