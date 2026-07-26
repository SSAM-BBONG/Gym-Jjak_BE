package com.ssambbong.gymjjak.chatbot.application.result;

import java.time.LocalDate;
import java.util.List;

/**
 * 챗봇 함수 호출의 운동 일지 조회 결과입니다.
 */
public record ChatbotWorkoutHistorySnapshot(
        LocalDate from,
        LocalDate to,
        List<ChatbotWorkoutDiarySnapshot> diaries
) {
}
