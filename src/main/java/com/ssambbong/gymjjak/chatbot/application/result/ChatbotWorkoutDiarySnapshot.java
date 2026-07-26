package com.ssambbong.gymjjak.chatbot.application.result;

import java.time.LocalDate;

/**
 * 운동 일지 한 건을 답변 근거로 전달하기 위한 축약 모델입니다.
 */
public record ChatbotWorkoutDiarySnapshot(
        LocalDate date,
        String exercise,
        String part,
        int setCount
) {
}
