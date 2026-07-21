package com.ssambbong.gymjjak.diet.application.port.out;

public interface MealAccessPort {

    boolean existsActivePtRelation(Long targetUserId, Long trainerUserId);
}
