package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out.domain;

import com.ssambbong.gymjjak.calendar.application.port.in.CalendarUsecase;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotInbodySnapshot;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotWorkoutHistorySnapshot;
import com.ssambbong.gymjjak.inbody.application.query.GetInbodyListQuery;
import com.ssambbong.gymjjak.inbody.application.result.InbodyItemResult;
import com.ssambbong.gymjjak.inbody.application.result.InbodyListResult;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotDomainQueryAdapterTest {

    @Mock private InbodyQueryUseCase inbodyQueryUseCase;
    @Mock private CalendarUsecase calendarUsecase;

    @Test
    void mapsTheLatestInbodyResultToTheChatbotSnapshot() {
        InbodyItemResult latest = new InbodyItemResult(
                1L, LocalDate.of(2026, 7, 23), new BigDecimal("170.0"), new BigDecimal("70.0"),
                new BigDecimal("20.0"), new BigDecimal("30.0"), null, null, null, null,
                null, null, null, null
        );
        when(inbodyQueryUseCase.getInbodyList(new GetInbodyListQuery(7L, null, null)))
                .thenReturn(new InbodyListResult(List.of(latest), null, null, false));

        ChatbotInbodySnapshot result = new ChatbotInbodyQueryAdapter(inbodyQueryUseCase).loadLatest(7L);

        assertThat(result.weight()).isEqualByComparingTo("70.0");
        assertThat(result.measuredDate()).isEqualTo(LocalDate.of(2026, 7, 23));
    }

    @Test
    void collectsOnlyWorkoutDiariesFromEachRequestedDay() {
        LocalDate date = LocalDate.of(2026, 7, 23);
        CalendarDayDiaryResult diary = new CalendarDayDiaryResult(1L, 10L, "squat", date, PartType.LEG, List.of());
        when(calendarUsecase.findCalendarDay(eq(7L), eq(7L), any(LocalDate.class)))
                .thenReturn(new CalendarDayResult(date, List.of(), List.of(diary)));

        ChatbotWorkoutHistorySnapshot result = new ChatbotWorkoutHistoryQueryAdapter(calendarUsecase)
                .loadHistory(7L, date, date);

        assertThat(result.diaries()).singleElement().satisfies(snapshot -> {
            assertThat(snapshot.exercise()).isEqualTo("squat");
            assertThat(snapshot.part()).isEqualTo("LEG");
        });
        verify(calendarUsecase).findCalendarDay(7L, 7L, date);
    }
}
