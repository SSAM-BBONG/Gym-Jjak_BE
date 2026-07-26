package com.ssambbong.gymjjak.diet.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "식단 부분 수정 요청. 전달한 필드만 변경됩니다.")
public class UpdateMealAnalysisRequest {

    @Schema(description = "변경할 식사 유형", example = "점심", nullable = true)
    private String mealType;

    @Schema(description = "변경할 식사 일시", example = "2026-07-18 12:30", type = "string", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime mealTime;

    @Schema(description = "변경할 메뉴", example = "닭가슴살 샐러드", nullable = true)
    @Size(max = 255, message = "메뉴는 255자 이하여야 합니다.")
    private String menu;

    @Schema(description = "변경할 열량. null이면 기존 값을 제거합니다.", example = "350", nullable = true)
    @PositiveOrZero(message = "kcal은 0 이상이어야 합니다.")
    private Long kcal;

    @Schema(description = "변경할 탄수화물(g). null이면 제거합니다.", example = "67.00", nullable = true)
    @PositiveOrZero(message = "탄수화물은 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 2, message = "탄수화물은 소수점 둘째 자리까지 입력할 수 있습니다.")
    private BigDecimal carbohydrate;

    @Schema(description = "변경할 단백질(g). null이면 제거합니다.", example = "51.90", nullable = true)
    @PositiveOrZero(message = "단백질은 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 2, message = "단백질은 소수점 둘째 자리까지 입력할 수 있습니다.")
    private BigDecimal protein;

    @Schema(description = "변경할 지방(g). null이면 제거합니다.", example = "7.60", nullable = true)
    @PositiveOrZero(message = "지방은 0 이상이어야 합니다.")
    @Digits(integer = 6, fraction = 2, message = "지방은 소수점 둘째 자리까지 입력할 수 있습니다.")
    private BigDecimal fat;

    @Schema(description = "새 식단 이미지 메타데이터. null이면 기존 이미지를 제거하고, 필드 생략 시 유지합니다.", nullable = true)
    @Valid
    private UploadedFileMetadataRequest file;

    @JsonIgnore @Schema(hidden = true) private boolean mealTypePresent;
    @JsonIgnore @Schema(hidden = true) private boolean mealTimePresent;
    @JsonIgnore @Schema(hidden = true) private boolean menuPresent;
    @JsonIgnore @Schema(hidden = true) private boolean kcalPresent;
    @JsonIgnore @Schema(hidden = true) private boolean carbohydratePresent;
    @JsonIgnore @Schema(hidden = true) private boolean proteinPresent;
    @JsonIgnore @Schema(hidden = true) private boolean fatPresent;
    @JsonIgnore @Schema(hidden = true) private boolean filePresent;

    @JsonSetter("mealType")
    public void setMealType(String mealType) { this.mealTypePresent = true; this.mealType = mealType; }

    @JsonSetter("mealTime")
    public void setMealTime(LocalDateTime mealTime) { this.mealTimePresent = true; this.mealTime = mealTime; }

    @JsonSetter("menu")
    public void setMenu(String menu) { this.menuPresent = true; this.menu = menu; }

    @JsonSetter("kcal")
    public void setKcal(Long kcal) { this.kcalPresent = true; this.kcal = kcal; }

    @JsonSetter("carbohydrate")
    public void setCarbohydrate(BigDecimal carbohydrate) { this.carbohydratePresent = true; this.carbohydrate = carbohydrate; }

    @JsonSetter("protein")
    public void setProtein(BigDecimal protein) { this.proteinPresent = true; this.protein = protein; }

    @JsonSetter("fat")
    public void setFat(BigDecimal fat) { this.fatPresent = true; this.fat = fat; }

    @JsonSetter("file")
    public void setFile(UploadedFileMetadataRequest file) { this.filePresent = true; this.file = file; }

    public boolean hasAnyField() {
        return mealTypePresent || mealTimePresent || menuPresent || kcalPresent
                || carbohydratePresent || proteinPresent || fatPresent || filePresent;
    }
}
