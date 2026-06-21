package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationCommandUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationDuplicateException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
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

    @Override
    public Long createPtReservation(CreatePtReservationCommand command) {
        log.debug("event=pt_reservation_create userId={}, ptCourseId={}, start={}, end={}",
                command.userId(), command.ptCourseId(),
                command.reservedStartAt(), command.reservedEndAt());

        PtCourse ptCourse = ptCourseRepository.findById(command.ptCourseId())
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

        log.info("event=pt_reservation_create_succeeded ptReservationId={}", saved.getId());
        return saved.getId();
    }
}
