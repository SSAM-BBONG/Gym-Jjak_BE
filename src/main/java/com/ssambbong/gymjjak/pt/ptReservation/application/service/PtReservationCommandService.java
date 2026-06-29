package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.global.infrastructure.cache.CalendarCacheEvictor;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CancelPtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.ChangePtReservationStatusCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationCommandUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationDuplicateException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationForbiddenException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationInvalidException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PtReservationCommandService implements PtReservationCommandUseCase {

    private final PtReservationRepository ptReservationRepository;
    private final PtCourseRepository ptCourseRepository;
    private final TrainerQueryPort trainerQueryPort;
    private final CalendarCacheEvictor calendarCacheEvictor;

    @Override
    public Long createPtReservation(CreatePtReservationCommand command) {
        log.debug("event=pt_reservation_create userId={}, ptCourseId={}, start={}, end={}",
                command.userId(), command.ptCourseId(),
                command.reservedStartAt(), command.reservedEndAt());

        PtCourse ptCourse = ptCourseRepository.findByIdForUpdate(command.ptCourseId())
                .orElseThrow(() -> {
                    log.warn("event=pt_reservation_create_failed reason=pt_course_not_found, ptCourseId={}",
                            command.ptCourseId());
                    return new PtCourseNotFoundException();
                });

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

        calendarCacheEvictor.evictMonth(
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

        // 상태 변경 (RESERVED 요청 시 도메인에서 예외 발생)
        reservation.changeStatus(command.status());
        ptReservationRepository.updateStatus(reservation);

        calendarCacheEvictor.evictMonth(
                reservationUserId,
                reservation.getReservedStartAt()
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

        // 상태 변경 — 종결 상태(COMPLETED/CANCELLED)이면 도메인에서 예외 발생, cancelledAt 자동 설정
        reservation.changeStatus(PtReservationStatus.CANCELLED);
        ptReservationRepository.updateStatus(reservation);

        calendarCacheEvictor.evictMonth(
                reservationUserId,
                reservation.getReservedStartAt()
        );

        log.info("event=pt_reservation_cancel_succeeded ptReservationId={}", command.ptReservationId());
        return reservation;
    }
}
