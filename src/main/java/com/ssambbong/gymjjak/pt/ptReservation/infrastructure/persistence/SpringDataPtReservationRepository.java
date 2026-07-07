package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtCalendarDayResult;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataPtReservationRepository extends JpaRepository<PtReservationJpaEntity, Long> {

    @Query("""
            SELECT COUNT(r) > 0 FROM PtReservationJpaEntity r
            WHERE r.ptCourseId = :ptCourseId
            AND r.status = com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.RESERVED
            AND r.reservedStartAt < :reservedEndAt
            AND r.reservedEndAt > :reservedStartAt
            """)
    boolean existsOverlappingReservation(
            @Param("ptCourseId") Long ptCourseId,
            @Param("reservedStartAt") LocalDateTime reservedStartAt,
            @Param("reservedEndAt") LocalDateTime reservedEndAt
    );

    // status 미지정 -> 전체 조회
    List<PtReservationJpaEntity> findAllByUserIdOrderByReservedStartAtDesc(Long userId);

    // status 지정 -> 필터
    List<PtReservationJpaEntity> findAllByUserIdAndStatusOrderByReservedStartAtDesc(Long userId, PtReservationStatus status);

    // ptCourse 도메인 강습별 활성 예약 수 배치 집계 (N+1 방지)
    @Query("""
            SELECT r.ptCourseId, COUNT(r)
            FROM PtReservationJpaEntity r
            WHERE r.ptCourseId IN :ptCourseIds
              AND r.status IN :statuses
            GROUP BY r.ptCourseId
            """)
    List<Object[]> countActiveGroupByPtCourseId(
            @Param("ptCourseIds") List<Long> ptCourseIds,
            @Param("statuses") List<PtReservationStatus> statuses);

    // ptCourse 도메인 강습별 전체 예약 수 배치 집계 (N+1 방지)
    @Query("""
            SELECT r.ptCourseId, COUNT(r)
            FROM PtReservationJpaEntity r
            WHERE r.ptCourseId IN :ptCourseIds
            GROUP BY r.ptCourseId
            """)
    List<Object[]> countTotalGroupByPtCourseId(@Param("ptCourseIds") List<Long> ptCourseIds);

    // 강습별 수강생 목록 조회 (최신 예약일순)
    List<PtReservationJpaEntity> findAllByPtCourseIdOrderByReservedStartAtDesc(Long ptCourseId);

    // 진행 중인 PT 수
    long countByStatus(PtReservationStatus status);

    // ── 메트릭용 집계 쿼리 ──

    // 취소된 예약 수 (cancelledAt IS NOT NULL)
    long countByCancelledAtIsNotNull();

    // progress_count 평균
    @Query("SELECT AVG(r.progressCount) FROM PtReservationJpaEntity r")
    Double findAverageProgressCount();

    @Query("""
    select new com.ssambbong.gymjjak.pt.ptReservation.application.result.PtCalendarDayResult(
        r.ptCourseId,
        c.title,
        r.reservedStartAt
    )
    from PtReservationJpaEntity r
    join PtCourseJpaEntity c on c.id = r.ptCourseId
    where r.userId = :userId
      and r.reservedStartAt >= :startAt
      and r.reservedStartAt < :endAt
      and r.status <> :cancelledStatus
      and r.cancelledAt is null
    order by r.reservedStartAt asc
""")
    List<PtCalendarDayResult> findCalendarDayPtsByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("cancelledStatus") PtReservationStatus cancelledStatus
    );

    @Query("""
    select r.reservedStartAt
    from PtReservationJpaEntity r
    where r.userId = :userId
      and r.reservedStartAt >= :startAt
      and r.reservedStartAt < :endAt
      and r.status <> :cancelledStatus
      and r.cancelledAt is null
    order by r.reservedStartAt asc
""")
    List<LocalDateTime> findReservedStartAtsByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("cancelledStatus") PtReservationStatus cancelledStatus
    );

    // 가용 날짜/시간 슬롯 계산용 — 강습의 기간 내 RESERVED 예약 시작 시각 목록
    @Query("""
        SELECT r.reservedStartAt
        FROM PtReservationJpaEntity r
        WHERE r.ptCourseId = :ptCourseId
          AND r.reservedStartAt >= :from
          AND r.reservedStartAt < :to
          AND r.status = com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.RESERVED
        """)
    List<LocalDateTime> findReservedStartAtsByPtCourseIdAndRange(
            @Param("ptCourseId") Long ptCourseId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // 대시보드 — 조직별 누적 이용자 수 (CANCELLED 제외 DISTINCT user_id)
    @Query(value = """
            SELECT COUNT(DISTINCT r.user_id)
            FROM pt_reservations r
            WHERE r.organization_id = :organizationId
              AND r.status != 'CANCELLED'
            """, nativeQuery = true)
    long countDistinctUsersByOrganizationId(@Param("organizationId") Long organizationId);

    // 대시보드 — 조직별 현재 이용자 수 (IN_PROGRESS DISTINCT user_id)
    @Query(value = """
            SELECT COUNT(DISTINCT r.user_id)
            FROM pt_reservations r
            WHERE r.organization_id = :organizationId
              AND r.status = 'IN_PROGRESS'
            """, nativeQuery = true)
    long countDistinctCurrentUsersByOrganizationId(@Param("organizationId") Long organizationId);

    // 리마인더 발송 대상 조회 — 지정 시간 범위 내 시작하는 RESERVED 상태 예약
    @Query("""
        SELECT r.userId, r.id
        FROM PtReservationJpaEntity r
        WHERE r.reservedStartAt >= :from
          AND r.reservedStartAt < :to
          AND r.status = com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.RESERVED
        """)
    List<Object[]> findReservationsStartingBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

}
