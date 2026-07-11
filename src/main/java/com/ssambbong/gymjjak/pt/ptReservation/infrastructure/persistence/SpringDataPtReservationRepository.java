package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtCalendarDayResult;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // [dashboard] 이용자 추이 — 주 단위 집계 (월요일 기준, 최근 1년)
    @Query(value = """
            SELECT DATE(DATE_SUB(r.reserved_start_at, INTERVAL WEEKDAY(r.reserved_start_at) DAY)) AS date,
                   COUNT(DISTINCT r.user_id)                                                        AS count
            FROM pt_reservations r
            WHERE r.organization_id = :organizationId
              AND r.status != 'CANCELLED'
              AND r.reserved_start_at >= :startDate
            GROUP BY date
            ORDER BY date
            """, nativeQuery = true)
    List<TrendPointRow> findWeeklyUserTrendByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate);

    // [dashboard] 이용자 추이 — 월 단위 집계 (최근 3년)
    @Query(value = """
            SELECT DATE(DATE_FORMAT(r.reserved_start_at, '%Y-%m-01')) AS date,
                   COUNT(DISTINCT r.user_id)                           AS count
            FROM pt_reservations r
            WHERE r.organization_id = :organizationId
              AND r.status != 'CANCELLED'
              AND r.reserved_start_at >= :startDate
            GROUP BY date
            ORDER BY date
            """, nativeQuery = true)
    List<TrendPointRow> findMonthlyUserTrendByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate);

    // [dashboard] 이용자 추이 — 3개월 단위 집계 (분기 기준, 최근 3년)
    @Query(value = """
            SELECT DATE(CONCAT(YEAR(r.reserved_start_at), '-', LPAD((QUARTER(r.reserved_start_at) - 1) * 3 + 1, 2, '0'), '-01')) AS date,
                   COUNT(DISTINCT r.user_id)                                                                                        AS count
            FROM pt_reservations r
            WHERE r.organization_id = :organizationId
              AND r.status != 'CANCELLED'
              AND r.reserved_start_at >= :startDate
            GROUP BY date
            ORDER BY date
            """, nativeQuery = true)
    List<TrendPointRow> findThreeMonthlyUserTrendByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate);

    // [dashboard] 이용자 추이 — 6개월 단위 집계 (1월/7월 기준, 최근 3년)
    @Query(value = """
            SELECT DATE(CONCAT(YEAR(r.reserved_start_at), '-', IF(MONTH(r.reserved_start_at) <= 6, '01', '07'), '-01')) AS date,
                   COUNT(DISTINCT r.user_id)                                                                              AS count
            FROM pt_reservations r
            WHERE r.organization_id = :organizationId
              AND r.status != 'CANCELLED'
              AND r.reserved_start_at >= :startDate
            GROUP BY date
            ORDER BY date
            """, nativeQuery = true)
    List<TrendPointRow> findSixMonthlyUserTrendByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate);

    // [dashboard] 조직 PT 수강생 목록 (IN_PROGRESS, 등록일 오름차순)
    @Query(value = """
            SELECT u.name                  AS userName,
                   r.created_at            AS enrolledAt,
                   r.progress_count        AS progressCount,
                   pc.total_session_count  AS totalSessionCount
            FROM pt_reservations r
            JOIN users u  ON r.user_id = u.user_id
            JOIN pt_courses pc ON r.pt_course_id = pc.pt_course_id
            WHERE r.pt_course_id = :ptCourseId
              AND r.organization_id = :organizationId
              AND r.status = 'IN_PROGRESS'
            ORDER BY r.created_at ASC
            """, nativeQuery = true)
    List<PtClientRow> findPtClientsByPtCourseId(
            @Param("ptCourseId") Long ptCourseId,
            @Param("organizationId") Long organizationId);

    // 유저+코스 기준 비취소 예약 수 (RESERVED + COMPLETED)
    int countByUserIdAndPtCourseIdAndStatusNot(Long userId, Long ptCourseId, PtReservationStatus status);

    // reservedEndAt이 지난 RESERVED 예약 일괄 COMPLETED 처리
    @Modifying
    @Query("""
        UPDATE PtReservationJpaEntity r
        SET r.status = com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.COMPLETED,
            r.completedAt = :now
        WHERE r.reservedEndAt <= :now
          AND r.status = com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus.RESERVED
        """)
    int bulkCompleteExpired(@Param("now") LocalDateTime now);

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

    // AdminDashboard 월별 예야된 pt 수 조회
    @Query(
            value = """
            select date_format(r.reserved_start_at, '%Y-%m') as month,
                   count(*) as count
            from pt_reservations r
            where r.status <> :cancelledStatus
              and r.cancelled_at is null
              and r.reserved_start_at >= :startDate
              and r.reserved_start_at < :endDate
            group by date_format(r.reserved_start_at, '%Y-%m')
            order by month asc
            """,
            nativeQuery = true
    )
    List<MonthlyPtReservationRow> findMonthlyPtReservations(
            @Param("cancelledStatus") String cancelledStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    interface MonthlyPtReservationRow {
        String getMonth();

        Long getCount();
    }
}
