package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarCacheEvictionPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CancelPtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.PaymentQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationErrorCode;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationStatusInvalidException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PtReservationCommandServiceCancelTest {

    private static final Long USER_ID = 1L;
    private static final Long COURSE_ID = 10L;

    @Mock
    private PtReservationRepository ptReservationRepository;
    @Mock
    private PtCourseRepository ptCourseRepository;
    @Mock
    private PtCourseScheduleRepository ptCourseScheduleRepository;
    @Mock
    private TrainerQueryPort trainerQueryPort;
    @Mock
    private PaymentQueryPort paymentQueryPort;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private CalendarCacheEvictionPort calendarCacheEvictionPort;

    private PtReservationCommandService service;

    @BeforeEach
    void setUp() {
        service = new PtReservationCommandService(
                ptReservationRepository,
                ptCourseRepository,
                ptCourseScheduleRepository,
                trainerQueryPort,
                paymentQueryPort,
                eventPublisher,
                calendarCacheEvictionPort,
                Clock.fixed(Instant.parse("2026-07-26T00:00:00Z"), ZoneOffset.UTC)
        );
    }

    @Test
    void allCancelledSessionsCannotBeCancelledAgain() {
        PtReservation requested = reservation(1L, PtReservationStatus.CANCELLED);
        when(ptReservationRepository.findById(1L)).thenReturn(java.util.Optional.of(requested));
        when(ptReservationRepository.findAllByUserIdAndPtCourseId(USER_ID, COURSE_ID))
                .thenReturn(List.of(requested, reservation(2L, PtReservationStatus.CANCELLED)));

        assertErrorCode(PtReservationErrorCode.PT_RESERVATION_ALREADY_CANCELLED);
        verify(ptReservationRepository, never()).bulkCancelByUserIdAndPtCourseId(USER_ID, COURSE_ID);
    }

    @Test
    void allCompletedSessionsCannotBeCancelled() {
        PtReservation requested = reservation(1L, PtReservationStatus.COMPLETED);
        when(ptReservationRepository.findById(1L)).thenReturn(java.util.Optional.of(requested));
        when(ptReservationRepository.findAllByUserIdAndPtCourseId(USER_ID, COURSE_ID))
                .thenReturn(List.of(requested, reservation(2L, PtReservationStatus.COMPLETED)));

        assertErrorCode(PtReservationErrorCode.PT_RESERVATION_ALREADY_COMPLETED);
        verify(ptReservationRepository, never()).bulkCancelByUserIdAndPtCourseId(USER_ID, COURSE_ID);
    }

    @Test
    void mixedTerminalSessionsCannotBeCancelled() {
        PtReservation requested = reservation(1L, PtReservationStatus.CANCELLED);
        when(ptReservationRepository.findById(1L)).thenReturn(java.util.Optional.of(requested));
        when(ptReservationRepository.findAllByUserIdAndPtCourseId(USER_ID, COURSE_ID))
                .thenReturn(List.of(requested, reservation(2L, PtReservationStatus.COMPLETED)));

        assertErrorCode(PtReservationErrorCode.PT_RESERVATION_CANNOT_CANCEL);
        verify(ptReservationRepository, never()).bulkCancelByUserIdAndPtCourseId(USER_ID, COURSE_ID);
    }

    @Test
    void cancellableSessionsAreCancelledWhileTerminalSessionsRemainUntouched() {
        PtReservation requested = reservation(1L, PtReservationStatus.COMPLETED);
        when(ptReservationRepository.findById(1L)).thenReturn(java.util.Optional.of(requested));
        when(ptReservationRepository.findAllByUserIdAndPtCourseId(USER_ID, COURSE_ID))
                .thenReturn(List.of(
                        requested,
                        reservation(2L, PtReservationStatus.CANCELLED),
                        reservation(3L, PtReservationStatus.RESERVED),
                        reservation(4L, PtReservationStatus.IN_PROGRESS)
                ));
        when(ptReservationRepository.bulkCancelByUserIdAndPtCourseId(USER_ID, COURSE_ID))
                .thenReturn(2);

        service.cancelPtReservation(new CancelPtReservationCommand(USER_ID, 1L));

        verify(ptReservationRepository).bulkCancelByUserIdAndPtCourseId(USER_ID, COURSE_ID);
    }

    @Test
    void zeroUpdatedSessionsDoesNotReturnSuccess() {
        PtReservation requested = reservation(1L, PtReservationStatus.RESERVED);
        when(ptReservationRepository.findById(1L)).thenReturn(java.util.Optional.of(requested));
        when(ptReservationRepository.findAllByUserIdAndPtCourseId(USER_ID, COURSE_ID))
                .thenReturn(List.of(requested));
        when(ptReservationRepository.bulkCancelByUserIdAndPtCourseId(USER_ID, COURSE_ID))
                .thenReturn(0);

        assertErrorCode(PtReservationErrorCode.PT_RESERVATION_CANNOT_CANCEL);
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    private void assertErrorCode(PtReservationErrorCode expected) {
        assertThatThrownBy(() ->
                service.cancelPtReservation(new CancelPtReservationCommand(USER_ID, 1L)))
                .isInstanceOf(PtReservationStatusInvalidException.class)
                .satisfies(exception -> {
                    PtReservationStatusInvalidException actual =
                            (PtReservationStatusInvalidException) exception;
                    org.assertj.core.api.Assertions.assertThat(actual.getErrorCode()).isEqualTo(expected);
                });
    }

    private PtReservation reservation(Long id, PtReservationStatus status) {
        LocalDateTime start = LocalDateTime.of(2026, 7, 27, 10, 0);
        return PtReservation.restore(
                id,
                USER_ID,
                COURSE_ID,
                100L,
                200L,
                start,
                start.plusHours(1),
                status == PtReservationStatus.CANCELLED ? start.minusDays(1) : null,
                status == PtReservationStatus.COMPLETED ? start.minusHours(1) : null,
                10,
                status
        );
    }
}
