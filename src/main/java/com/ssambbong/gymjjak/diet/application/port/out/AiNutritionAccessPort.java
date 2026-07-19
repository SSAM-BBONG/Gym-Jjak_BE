package com.ssambbong.gymjjak.diet.application.port.out;

public interface AiNutritionAccessPort {
    // 사용자가 현재 AI 영양분석 기능을 사용할 수 있는지 확인한다.
    boolean hasActiveAccess(Long userId);
}
