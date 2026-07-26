package com.ssambbong.gymjjak.chatbot.application.result;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * LLM 답변 생성에 필요한 최신 인바디 측정값만 담는 읽기 전용 DTO입니다.
 */
public record ChatbotInbodySnapshot(
        LocalDate measuredDate,
        BigDecimal weight,
        BigDecimal bodyFatPercentage,
        BigDecimal skeletalMuscleMass
) {
}
