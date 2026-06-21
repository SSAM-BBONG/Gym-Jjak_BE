package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtReservationCountQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PtReservationCountQueryAdapter implements PtReservationCountQueryPort {

    private final SpringDataPtReservationRepository ptReservationRepository;

    @Override
    public int countActiveByPtCourseId(Long ptCourseId) {
        return ptReservationRepository.countByPtCourseIdAndStatusIn(
                ptCourseId,
                List.of(PtReservationStatus.RESERVED, PtReservationStatus.IN_PROGRESS)
        );
    }

    @Override
    public int countTotalByPtCourseId(Long ptCourseId) {
        return (int) ptReservationRepository.countByPtCourseId(ptCourseId);
    }
}
