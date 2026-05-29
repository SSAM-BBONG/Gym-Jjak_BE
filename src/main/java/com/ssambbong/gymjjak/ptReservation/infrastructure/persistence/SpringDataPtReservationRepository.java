package com.ssambbong.gymjjak.ptReservation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SpringDataPtReservationRepository extends JpaRepository<PtReservationJpaEntity, Long> {

    @Query("""
            SELECT COUNT(r) > 0 FROM PtReservationJpaEntity r
            WHERE r.ptCourseId = :ptCourseId
            AND r.status = 'RESERVED'
            AND r.reservedStartAt < :reservedEndAt
            AND r.reservedEndAt > :reservedStartAt
            """)
    boolean existsOverlappingReservation(
            @Param("ptCourseId") Long ptCourseId,
            @Param("reservedStartAt") LocalDateTime reservedStartAt,
            @Param("reservedEndAt") LocalDateTime reservedEndAt
    );

}
