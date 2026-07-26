package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out.domain;

import com.ssambbong.gymjjak.calendar.application.port.in.CalendarUsecase;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotWorkoutHistoryQueryPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotWorkoutDiarySnapshot;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotWorkoutHistorySnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 챗봇 포트를 캘린더 도메인의 일별 조회 UseCase에 연결하는 Adapter입니다.
 */
@Component
@RequiredArgsConstructor
public class ChatbotWorkoutHistoryQueryAdapter implements ChatbotWorkoutHistoryQueryPort {

    private final CalendarUsecase calendarUsecase;

    @Override
    public ChatbotWorkoutHistorySnapshot loadHistory(Long userId, LocalDate from, LocalDate to) {
        List<ChatbotWorkoutDiarySnapshot> diaries = new ArrayList<>();

        // 기간은 서비스에서 최대 31일로 제한했으므로 기존 일별 UseCase를 안전하게 재사용합니다.
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            calendarUsecase.findCalendarDay(userId, userId, date)
                    .diaries()
                    .stream()
                    .map(this::toSnapshot)
                    .forEach(diaries::add);
        }
        return new ChatbotWorkoutHistorySnapshot(from, to, List.copyOf(diaries));
    }

    /** 캘린더의 상세 세트 정보 대신 LLM에 필요한 운동 요약만 전달합니다. */
    private ChatbotWorkoutDiarySnapshot toSnapshot(CalendarDayDiaryResult diary) {
        return new ChatbotWorkoutDiarySnapshot(
                diary.date(),
                diary.exercise(),
                diary.part().name(),
                diary.sets().size()
        );
    }
}
