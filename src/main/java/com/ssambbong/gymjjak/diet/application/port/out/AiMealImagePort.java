package com.ssambbong.gymjjak.diet.application.port.out;

public interface AiMealImagePort {
    String resolveAccessibleImageUrl(Long fileId, Long userId);
}
