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

/*
* 비즈니스 로직
* 1. PT 강습 존재 여부 확인
* 2. 중복 예약 여부 확인
* 3. 예약 생성 및 저장
* */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PtReservationCommandService implements PtReservationCommandUseCase {

    private final PtReservationRepository ptReservationRepository;
    private final PtCourseRepository ptCourseRepository;

    @Override
    public Long createPtReservation(CreatePtReservationCommand command) {

        log.debug("[PtReservationCreate] userId={}, ptCourseId={}, start={}, end={}",
                command.userId(), command.ptCourseId(),
                command.reservedStartAt(), command.reservedEndAt());

        // 1. PT 강습 조회 → organizationId, trainerProfileId, totalSessionCount 가져오기
        PtCourse ptCourse = ptCourseRepository.findById(command.ptCourseId())
                .orElseThrow(() -> {
                    log.warn("[PtReservationCreate] 존재하지 않는 PT 강습 - ptCourseId={}", command.ptCourseId());
                    return new PtCourseNotFoundException();
                });

        // 2. 중복 예약 확인 → 같은 PT 강습 + 시간 겹치면 예외
        if (ptReservationRepository.existsByPtCourseIdAndTimeOverlap(
                command.ptCourseId(),
                command.reservedStartAt(),
                command.reservedEndAt()
        )) {
            log.warn("[PtReservationCreate] 중복 예약 시도 감지 - userId={}, ptCourseId={}, start={}, end={}",
                    command.userId(), command.ptCourseId(), command.reservedStartAt(), command.reservedEndAt());
            throw new PtReservationDuplicateException();
        }

        // 3. 도메인 객체 생성
        PtReservation ptReservation = PtReservation.create(
                command.userId(),
                command.ptCourseId(),
                ptCourse.getOrganizationId(),       // pt_course에서 복사
                ptCourse.getTrainerProfileId(),     // pt_course에서 복사
                command.reservedStartAt(),
                command.reservedEndAt(),
                ptCourse.getTotalSessionCount()     // pt_course에서 복사
        );

        // 4. 저장 후 id 반환
        PtReservation saved = ptReservationRepository.save(ptReservation);

        log.info("[PtReservationCreate] ptReservationId={}", saved.getId());
        return saved.getId();
    }
}
