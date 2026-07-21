package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.PtReservationJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MealAccessJpaRepository extends Repository<PtReservationJpaEntity, Long> {

    @Query("""
            select count(r) > 0
            from PtReservationJpaEntity r
            join TrainerProfileJpaEntity tp on tp.trainerProfileId = r.trainerProfileId
            where r.userId = :targetUserId
              and tp.userId = :trainerUserId
              and tp.status = com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus.ACTIVE
              and r.status in (
                  com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.RESERVED,
                  com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.IN_PROGRESS
              )
              and r.cancelledAt is null
            """)
    boolean existsActivePtRelation(
            @Param("targetUserId") Long targetUserId,
            @Param("trainerUserId") Long trainerUserId);
}
