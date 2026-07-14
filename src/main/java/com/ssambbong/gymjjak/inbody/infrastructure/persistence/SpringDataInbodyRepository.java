package com.ssambbong.gymjjak.inbody.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SpringDataInbodyRepository extends JpaRepository<InbodyJpaEntity, Long> {

    boolean existsByUserIdAndMeasuredDate(Long userId, LocalDate measuredDate);

    @Query("""
            SELECT i
            FROM InbodyJpaEntity i
            WHERE i.userId = :userId
              AND (
                    :measuredDate IS NULL
                    OR i.measuredDate < :measuredDate
                    OR (i.measuredDate = :measuredDate AND i.id < :inbodyId)
              )
            ORDER BY i.measuredDate DESC, i.id DESC
            """)
    List<InbodyJpaEntity> findInbodySlice(
            @Param("userId") Long userId,
            @Param("measuredDate") LocalDate measuredDate,
            @Param("inbodyId") Long inbodyId,
            Pageable pageable
    );
}
