package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import org.springframework.stereotype.Component;

@Component
public class PtReservationPersistenceMapper {

    public PtReservation toDomain(PtReservationJpaEntity entity) {
        return PtReservation.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getPtCourseId(),
                entity.getOrganizationId(),
                entity.getTrainerProfileId(),
                entity.getReservedStartAt(),
                entity.getReservedEndAt(),
                entity.getCancelledAt(),
                entity.getCompletedAt(),
                entity.getProgressCount(),
                entity.getTotalSessionCount(),
                entity.getStatus()
        );
    }
}
