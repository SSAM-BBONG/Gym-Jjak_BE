package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarCacheEvictionPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CancelPtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.ChangePtReservationStatusCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.event.PtReservationApprovedEvent;
import com.ssambbong.gymjjak.pt.ptReservation.application.event.PtReservationCanceledEvent;
import com.ssambbong.gymjjak.pt.ptReservation.application.event.PtReservationRequestedEvent;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationCommandUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationDuplicateException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationForbiddenException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationInvalidException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationScheduleMismatchException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PtReservationCommandService implements PtReservationCommandUseCase {

    private final PtReservationRepository ptReservationRepository;
    private final PtCourseRepository ptCourseRepository;
    private final PtCourseScheduleRepository ptCourseScheduleRepository;
    private final TrainerQueryPort trainerQueryPort;
    private final ApplicationEventPublisher eventPublisher;
    private final CalendarCacheEvictionPort calendarCacheEvictionPort;

    @Override
    public Long createPtReservation(CreatePtReservationCommand command) {
        log.debug("event=pt_reservation_create userId={}, ptCourseId={}, start={}, end={}",
                command.userId(), command.ptCourseId(),
                command.reservedStartAt(), command.reservedEndAt());

        // FOR UPDATE로 잠금 획득 — 동시 예약 요청 시 한 번에 하나씩 처리
        PtCourse ptCourse = ptCourseRepository.findByIdForUpdate(command.ptCourseId())
                .orElseThrow(() -> {
                    log.warn("event=pt_reservation_create_failed " +
                                    "reason=pt_course_not_found, ptCourseId={}",
                            command.ptCourseId());
                    return new PtCourseNotFoundException();
                });

        // 예약 시간 null 및 시간 순서 검증 (종료 > 시작이어야 함)
        if (command.reservedStartAt() == null || command.reservedEndAt() == null
                || !command.reservedEndAt().isAfter(command.reservedStartAt())) {
            log.warn("event=pt_reservation_create_failed " +
                            "reason=invalid_time userId={} start={} end={}",
                    command.userId(), command.reservedStartAt(), command.reservedEndAt());
            throw new PtReservationInvalidException();
        }

        validateSchedule(ptCourse.getId(), command.reservedStartAt(), command.reservedEndAt());

        if (ptReservationRepository.existsByPtCourseIdAndTimeOverlap(
                command.ptCourseId(),
                command.reservedStartAt(),
                command.reservedEndAt()
        )) {
            log.warn("event=pt_reservation_create_failed reason=duplicate, userId={}, ptCourseId={}, start={}, end={}",
                    command.userId(), command.ptCourseId(), command.reservedStartAt(), command.reservedEndAt());
            throw new PtReservationDuplicateException();
        }

        PtReservation ptReservation = PtReservation.create(
                command.userId(),
                command.ptCourseId(),
                ptCourse.getOrganizationId(),
                ptCourse.getTrainerProfileId(),
                command.reservedStartAt(),
                command.reservedEndAt(),
                ptCourse.getTotalSessionCount()
        );

        PtReservation saved = ptReservationRepository.save(ptReservation);

        // 예약 생성 즉시 확정 - 예약 회원에게 확정 알림 발행
        eventPublisher.publishEvent(
                new PtReservationApprovedEvent(
                saved.getUserId(),
                saved.getId()
        ));

        // 트레이너에게 새 예약 신청 알림 (트레이너 userId 조회 실패 시 알림 생략, 예약은 정상 처리)
        trainerQueryPort.findUserIdByTrainerProfileId(ptCourse.getTrainerProfileId())
                .ifPresent(trainerUserId -> eventPublisher.publishEvent(
                        new PtReservationRequestedEvent(trainerUserId, saved.getId())));

        evictMonthAfterCommit(
                command.userId(),
                command.reservedStartAt()
        );

        log.info("event=pt_reservation_create_succeeded ptReservationId={}", saved.getId());
        return saved.getId();
    }

    @Override
    public PtReservation changePtReservationStatus(ChangePtReservationStatusCommand command) {
        if (command.userId() == null || command.ptReservationId() == null || command.status() == null) {
            throw new PtReservationInvalidException();
        }
        log.debug("event=pt_reservation_status_change userId={}, ptReservationId={}, status={}",
                command.userId(), command.ptReservationId(), command.status());

        // 예약 조회
        PtReservation reservation = ptReservationRepository.findById(command.ptReservationId())
                .orElseThrow(() -> {
                    log.warn("event=pt_reservation_status_change_failed reason=not_found, ptReservationId={}",
                            command.ptReservationId());
                    return new PtReservationNotFoundException();
                });

        // 트레이너 프로필 조회 (트레이너만 변경 가능)
        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(command.userId())
                .orElseThrow(() -> {
                    log.warn("event=pt_reservation_status_change_failed reason=forbidden, userId={}",
                            command.userId());
                    return new PtReservationForbiddenException();
                });

        // 본인 예약인지 확인 (본인 강습의 예약만 상태 변경 가능)
        if (!reservation.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=pt_reservation_status_change_failed reason=forbidden, userId={}, ptReservationId={}",
                    command.userId(), command.ptReservationId());
            throw new PtReservationForbiddenException();
        }

        Long reservationUserId = reservation.getUserId();
        LocalDateTime reservedStartAt = reservation.getReservedStartAt();

        // 상태 변경 (RESERVED 요청 시 도메인에서 예외 발생)
        reservation.changeStatus(command.status());
        ptReservationRepository.updateStatus(reservation);

        // 예약 취소 시 알림 발송
        if (command.status() == PtReservationStatus.CANCELLED) {
            eventPublisher.publishEvent(
                    new PtReservationCanceledEvent(
                    reservationUserId, command.ptReservationId()
            ));
        }

        evictMonthAfterCommit(
                reservationUserId,
                reservedStartAt
        );

        log.info("event=pt_reservation_status_change_succeeded ptReservationId={}, status={}",
                command.ptReservationId(), command.status());

        return reservation;
    }

    @Override
    public PtReservation cancelPtReservation(CancelPtReservationCommand command) {
        // controller 외 진입점(batch, event handler 등) 방어
        if (command.userId() == null || command.ptReservationId() == null) {
            throw new PtReservationInvalidException();
        }
        log.debug("event=pt_reservation_cancel_started userId={} ptReservationId={}",
                command.userId(), command.ptReservationId());

        // 예약 존재 여부 확인
        PtReservation reservation = ptReservationRepository.findById(command.ptReservationId())
                .orElseThrow(() -> {
                    log.warn("event=pt_reservation_cancel_failed reason=not_found ptReservationId={}",
                            command.ptReservationId());
                    return new PtReservationNotFoundException();
                });

        // 본인 예약 여부 확인
        if (!reservation.getUserId().equals(command.userId())) {
            log.warn("event=pt_reservation_cancel_failed reason=forbidden userId={} ptReservationId={}",
                    command.userId(), command.ptReservationId());
            throw new PtReservationForbiddenException();
        }

        Long reservationUserId = reservation.getUserId();
        LocalDateTime reservedStartAt = reservation.getReservedStartAt();

        // 상태 변경 — 종결 상태(COMPLETED/CANCELLED)이면 도메인에서 예외 발생, cancelledAt 자동 설정
        reservation.changeStatus(PtReservationStatus.CANCELLED);
        ptReservationRepository.updateStatus(reservation);

        evictMonthAfterCommit(
                reservationUserId,
                reservedStartAt
        );

        log.info("event=pt_reservation_cancel_succeeded ptReservationId={}", command.ptReservationId());
        return reservation;
    }

    private void validateSchedule(Long ptCourseId, LocalDateTime reservedStartAt, LocalDateTime reservedEndAt) {
        DayOfWeek day = reservedStartAt.getDayOfWeek();
        LocalTime startTime = reservedStartAt.toLocalTime();
        LocalTime endTime = reservedEndAt.toLocalTime();

        List<PtCourseSchedule> schedules = ptCourseScheduleRepository.findAllByPtCourseId(ptCourseId);
        boolean matched = schedules.stream().anyMatch(s ->
                s.getDayOfWeek() == day &&
                s.getStartTime().equals(startTime) &&
                s.getEndTime().equals(endTime)
        );

        if (!matched) {
            log.warn("event=pt_reservation_create_failed reason=schedule_mismatch ptCourseId={} day={} start={} end={}",
                    ptCourseId, day, startTime, endTime);
            throw new PtReservationScheduleMismatchException();
        }
    }

    private void evictMonthAfterCommit(
            Long userId,
            LocalDateTime reservedStartAt
    ) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            calendarCacheEvictionPort.evictMonth(
                                    userId,
                                    reservedStartAt
                            );
                        }
                    }
            );
            return;
        }

        calendarCacheEvictionPort.evictMonth(
                userId,
                reservedStartAt
        );
    }
}
