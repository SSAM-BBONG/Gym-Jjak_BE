package com.ssambbong.gymjjak.pt.feedback.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PtReservationQueryAdapter implements PtReservationQueryPort {

    private final PtReservationRepository ptReservationRepository;

    @Override
    public ReservationInfo findById(Long ptReservationId) {
        return ptReservationRepository.findById(ptReservationId)
                .map(r -> new ReservationInfo(
                        r.getPtCourseId(),
                        r.getTrainerProfileId(),
                        r.getUserId(),
                        r.getStatus(),
                        r.getReservedEndAt()
                ))
                .orElseThrow(FeedbackNotFoundException::new);
    }

    @Override
    public List<Long> findReservationIdsByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return ptReservationRepository.findAllByUserId(userId, null)
                .stream()
                .filter(r -> r.getPtCourseId().equals(ptCourseId))
                .map(PtReservation::getId)
                .toList();
    }
}
