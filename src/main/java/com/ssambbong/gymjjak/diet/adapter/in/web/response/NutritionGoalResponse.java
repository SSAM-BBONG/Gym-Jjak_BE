package com.ssambbong.gymjjak.diet.adapter.in.web.response;

import java.time.LocalDateTime;

public record NutritionGoalResponse(Long goalId, Long goalProtein, Long goalCarbohydrate,
                                    Long goalFat, Long dailyGoalKcal,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {}
