package com.ssambbong.gymjjak.diet.adapter.in.web;

import com.ssambbong.gymjjak.diet.domain.exception.InvalidMealTypeException;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MealTypeMapper {

    private static final Map<String, MealType> TO_ENUM = Map.of(
            "아침", MealType.BREAKFAST,
            "점심", MealType.LUNCH,
            "저녁", MealType.DINNER,
            "간식", MealType.SNACK
    );
    private static final Map<MealType, String> TO_KOREAN = Map.of(
            MealType.BREAKFAST, "아침",
            MealType.LUNCH, "점심",
            MealType.DINNER, "저녁",
            MealType.SNACK, "간식"
    );

    public MealType toEnum(String value) {
        MealType mealType = TO_ENUM.get(value);
        if (mealType == null) {
            throw new InvalidMealTypeException(value);
        }
        return mealType;
    }

    public String toKorean(MealType mealType) {
        return TO_KOREAN.get(mealType);
    }
}
