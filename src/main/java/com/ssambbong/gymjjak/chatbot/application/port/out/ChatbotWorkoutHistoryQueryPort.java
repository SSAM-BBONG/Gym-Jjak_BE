package com.ssambbong.gymjjak.chatbot.application.port.out;

import com.ssambbong.gymjjak.chatbot.application.result.ChatbotWorkoutHistorySnapshot;

import java.time.LocalDate;

/**
 * 챗봇이 운동 일지 도메인에서 기간별 기록을 읽기 위한 출력 포트입니다.
 */
public interface ChatbotWorkoutHistoryQueryPort {

    ChatbotWorkoutHistorySnapshot loadHistory(Long userId, LocalDate from, LocalDate to);
}
