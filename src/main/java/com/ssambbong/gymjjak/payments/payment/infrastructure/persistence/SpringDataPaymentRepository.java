package com.ssambbong.gymjjak.payments.payment.infrastructure.persistence;

import com.ssambbong.gymjjak.payments.payment.domain.model.PaymentStatus;
import com.ssambbong.gymjjak.payments.payment.domain.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, Long> {

    // 웹훅 수신 시 orderId로 결제 건 조회
    Optional<PaymentJpaEntity> findByOrderId(String orderId);

    // 내 결제 내역 목록 조회 (최신순)
    List<PaymentJpaEntity> findAllByUserIdOrderByIdDesc(Long userId);

    // PT 코스 중복 결제 검증
    boolean existsByUserIdAndPtCourseIdAndStatus(Long userId, Long ptCourseId, PaymentStatus status);

    // 구독 결제 PENDING 중복 검증
    boolean existsByUserIdAndProductTypeAndStatus(Long userId, ProductType productType, PaymentStatus status);

    // [dashboard] 조직 이번 달 매출
    @Query(value = """
            SELECT COALESCE(SUM(p.amount), 0)
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
              AND pc.deleted_at IS NULL
              AND p.status = 'PAID'
              AND p.paid_at >= :startOfMonth
              AND p.paid_at < :startOfNextMonth
            """, nativeQuery = true)
    long sumThisMonthRevenueByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("startOfNextMonth") LocalDateTime startOfNextMonth
    );

    // [dashboard] 조직 누적 매출
    @Query(value = """
            SELECT COALESCE(SUM(p.amount), 0)
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
              AND pc.deleted_at IS NULL
              AND p.status = 'PAID'
            """, nativeQuery = true)
    long sumTotalRevenueByOrganizationId(@Param("organizationId") Long organizationId);

    // [dashboard] 조직 주별 매출 (월요일 기준, 최근 1년)
    @Query(value = """
            SELECT DATE(DATE_SUB(p.paid_at, INTERVAL WEEKDAY(p.paid_at) DAY)) AS date,
                   COALESCE(SUM(p.amount), 0)                                  AS amount
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
              AND pc.deleted_at IS NULL
              AND p.status = 'PAID'
              AND p.paid_at >= :startDate
            GROUP BY date
            ORDER BY date ASC
            """, nativeQuery = true)
    List<MonthlyRevenueRow> findWeeklyRevenueByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate
    );

    // [dashboard] 조직 월별 매출 (최근 N개월)
    @Query(value = """
            SELECT DATE_FORMAT(p.paid_at, '%Y-%m-01') AS date,
                   COALESCE(SUM(p.amount), 0)         AS amount
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
              AND pc.deleted_at IS NULL
              AND p.status = 'PAID'
              AND p.paid_at >= :startDate
            GROUP BY DATE_FORMAT(p.paid_at, '%Y-%m-01')
            ORDER BY date ASC
            """, nativeQuery = true)
    List<MonthlyRevenueRow> findMonthlyRevenueByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate
    );

    // [dashboard] 조직 3개월별 매출 (분기 기준, 최근 3년)
    @Query(value = """
            SELECT DATE(CONCAT(YEAR(p.paid_at), '-', LPAD((QUARTER(p.paid_at) - 1) * 3 + 1, 2, '0'), '-01')) AS date,
                   COALESCE(SUM(p.amount), 0)                                                                  AS amount
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
              AND pc.deleted_at IS NULL
              AND p.status = 'PAID'
              AND p.paid_at >= :startDate
            GROUP BY date
            ORDER BY date ASC
            """, nativeQuery = true)
    List<MonthlyRevenueRow> findThreeMonthlyRevenueByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate
    );

    // [dashboard] 조직 6개월별 매출 (1월/7월 기준, 최근 3년)
    @Query(value = """
            SELECT DATE(CONCAT(YEAR(p.paid_at), '-', IF(MONTH(p.paid_at) <= 6, '01', '07'), '-01')) AS date,
                   COALESCE(SUM(p.amount), 0)                                                        AS amount
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
              AND pc.deleted_at IS NULL
              AND p.status = 'PAID'
              AND p.paid_at >= :startDate
            GROUP BY date
            ORDER BY date ASC
            """, nativeQuery = true)
    List<MonthlyRevenueRow> findSixMonthlyRevenueByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDateTime startDate
    );

    // [dashboard] 조직 트레이너별 매출 (이번 달 + 누적)
    @Query(value = """
            SELECT pc.trainer_profile_id                                              AS trainerProfileId,
                   tp.trainer_name                                                    AS trainerName,
                   COALESCE(SUM(CASE
                       WHEN p.paid_at >= :startOfMonth AND p.paid_at < :startOfNextMonth
                       THEN p.amount ELSE 0 END), 0)                                 AS thisMonthAmount,
                   COALESCE(SUM(p.amount), 0)                                        AS totalAmount
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            JOIN trainer_profiles tp ON pc.trainer_profile_id = tp.trainer_profile_id
            WHERE pc.organization_id = :organizationId
              AND pc.deleted_at IS NULL
              AND p.status = 'PAID'
            GROUP BY pc.trainer_profile_id, tp.trainer_name
            ORDER BY pc.trainer_profile_id ASC
            """, nativeQuery = true)
    List<TrainerRevenueRow> findTrainerRevenueByOrganizationId(
            @Param("organizationId") Long organizationId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("startOfNextMonth") LocalDateTime startOfNextMonth
    );

    interface MonthlyRevenueRow {
        LocalDate getDate();
        Long getAmount();
    }

    interface TrainerRevenueRow {
        Long getTrainerProfileId();
        String getTrainerName();
        Long getThisMonthAmount();
        Long getTotalAmount();
    }
}
