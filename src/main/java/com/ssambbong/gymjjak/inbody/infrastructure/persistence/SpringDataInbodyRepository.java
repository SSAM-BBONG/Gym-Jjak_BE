package com.ssambbong.gymjjak.inbody.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SpringDataInbodyRepository extends JpaRepository<InbodyJpaEntity, Long> {

    boolean existsByUserIdAndMeasuredDate(Long userId, LocalDate measuredDate);
}
