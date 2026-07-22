package com.ssambbong.gymjjak.calendar.adapter.out.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.PtReservationJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarPtReservationJpaRepository extends Repository<PtReservationJpaEntity, Long> {

    @Query("""
        select r.ptCourseId as ptId,
               c.title as title,
               r.reservedStartAt as reservedStartAt
        from PtReservationJpaEntity r
        join PtCourseJpaEntity c on c.id = r.ptCourseId
        where r.userId = :userId
          and r.reservedStartAt >= :startAt
          and r.reservedStartAt < :endAt
          and r.status <> com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.CANCELLED
          and r.cancelledAt is null
        order by r.reservedStartAt asc
    """)
    List<CalendarDayPtRow> findCalendarDayPtsByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
        select r.reservedStartAt
        from PtReservationJpaEntity r
        where r.userId = :userId
          and r.reservedStartAt >= :startAt
          and r.reservedStartAt < :endAt
          and r.status <> com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.CANCELLED
          and r.cancelledAt is null
        order by r.reservedStartAt asc
    """)
    List<LocalDateTime> findReservedStartAtsByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

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
    boolean existsActivePtRelationWithTrainer(
            @Param("targetUserId") Long targetUserId,
            @Param("trainerUserId") Long trainerUserId
    );

    interface CalendarDayPtRow {

        Long getPtId();

        String getTitle();

        LocalDateTime getReservedStartAt();
    }
}
