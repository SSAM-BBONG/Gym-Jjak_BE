package com.ssambbong.gymjjak.calendar;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPtReservationPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;
import com.ssambbong.gymjjak.calendar.application.service.CalendarMonthReader;
import com.ssambbong.gymjjak.calendar.application.service.CalendarService;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private CalendarPtReservationPort calendarPtReservationPort;

    @Mock
    private WorkoutDiaryPort workoutDiaryPort;

    @Mock
    private CalendarMonthReader calendarMonthReader;

    @InjectMocks
    private CalendarService calendarService;

    @Test
    @DisplayName("본인 일별 캘린더는 PT 관계 검증 없이 조회한다")
    void findCalendarDay_self_success() {
        LocalDate date = LocalDate.of(2026, 7, 15);

        when(calendarPtReservationPort.findPtsByUserIdAndDate(1L, date))
                .thenReturn(List.of());
        when(workoutDiaryPort.findDiariesByUserIdAndDate(1L, date))
                .thenReturn(List.of());

        calendarService.findCalendarDay(1L, 1L, date);

        verify(calendarPtReservationPort, never())
                .existsActivePtRelationWithTrainer(1L, 1L);
        verify(calendarPtReservationPort).findPtsByUserIdAndDate(1L, date);
        verify(workoutDiaryPort).findDiariesByUserIdAndDate(1L, date);
    }

    @Test
    @DisplayName("활성 PT 관계가 있는 트레이너는 회원 일별 캘린더를 조회한다")
    void findCalendarDay_trainer_success() {
        LocalDate date = LocalDate.of(2026, 7, 15);

        when(calendarPtReservationPort.existsActivePtRelationWithTrainer(1L, 10L))
                .thenReturn(true);
        when(calendarPtReservationPort.findPtsByUserIdAndDate(1L, date))
                .thenReturn(List.of());
        when(workoutDiaryPort.findDiariesByUserIdAndDate(1L, date))
                .thenReturn(List.of());

        calendarService.findCalendarDay(10L, 1L, date);

        verify(calendarPtReservationPort).existsActivePtRelationWithTrainer(1L, 10L);
        verify(calendarPtReservationPort).findPtsByUserIdAndDate(1L, date);
        verify(workoutDiaryPort).findDiariesByUserIdAndDate(1L, date);
    }

    @Test
    @DisplayName("활성 PT 관계가 없으면 다른 회원 일별 캘린더를 조회할 수 없다")
    void findCalendarDay_accessDenied() {
        LocalDate date = LocalDate.of(2026, 7, 15);

        when(calendarPtReservationPort.existsActivePtRelationWithTrainer(1L, 10L))
                .thenReturn(false);

        assertThatThrownBy(() -> calendarService.findCalendarDay(10L, 1L, date))
                .isInstanceOf(CalendarException.class)
                .satisfies(exception -> {
                    CalendarException calendarException = (CalendarException) exception;
                    org.assertj.core.api.Assertions.assertThat(calendarException.getErrorCode())
                            .isEqualTo(CalendarErrorCode.CALENDAR_ACCESS_DENIED);
                });

        verify(calendarPtReservationPort).existsActivePtRelationWithTrainer(1L, 10L);
        verify(calendarPtReservationPort, never()).findPtsByUserIdAndDate(1L, date);
        verify(workoutDiaryPort, never()).findDiariesByUserIdAndDate(1L, date);
    }

    @Test
    @DisplayName("월별 캘린더는 권한 검증 후 reader를 통해 조회한다")
    void findCalendarMonth_trainer_success() {
        CalendarMonthResult result = new CalendarMonthResult(2026, 7, List.of());

        when(calendarPtReservationPort.existsActivePtRelationWithTrainer(1L, 10L))
                .thenReturn(true);
        when(calendarMonthReader.findCalendarMonth(1L, 2026, 7))
                .thenReturn(result);

        calendarService.findCalendarMonth(10L, 1L, 2026, 7);

        verify(calendarPtReservationPort).existsActivePtRelationWithTrainer(1L, 10L);
        verify(calendarMonthReader).findCalendarMonth(1L, 2026, 7);
    }
}
