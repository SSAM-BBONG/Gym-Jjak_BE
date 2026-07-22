package com.ssambbong.gymjjak.diet.application.port.out;

public interface MealImageUrlPort {

    String resolve(Long fileId, Long mealOwnerUserId);
}
