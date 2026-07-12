package com.ssambbong.gymjjak.payments.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, Long> {

    // [dashboard] 조직 이번 달 매출
    @Query(value = """
            SELECT COALESCE(SUM(p.amount), 0)
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
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
              AND p.status = 'PAID'
            """, nativeQuery = true)
    long sumTotalRevenueByOrganizationId(@Param("organizationId") Long organizationId);

    // [dashboard] 조직 월별 매출 (최근 N개월)
    @Query(value = """
            SELECT DATE_FORMAT(p.paid_at, '%Y-%m-01') AS date,
                   COALESCE(SUM(p.amount), 0)         AS amount
            FROM payments p
            JOIN pt_courses pc ON p.pt_course_id = pc.pt_course_id
            WHERE pc.organization_id = :organizationId
              AND p.status = 'PAID'
              AND p.paid_at >= :startDate
            GROUP BY DATE_FORMAT(p.paid_at, '%Y-%m-01')
            ORDER BY date ASC
            """, nativeQuery = true)
    List<MonthlyRevenueRow> findMonthlyRevenueByOrganizationId(
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
              AND p.status = 'PAID'
            GROUP BY pc.trainer_profile_id, tp.trainer_name
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
