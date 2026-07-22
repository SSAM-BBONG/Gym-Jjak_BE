package com.ssambbong.gymjjak.inbody.domain.repository;

import com.ssambbong.gymjjak.inbody.domain.model.Inbody;

import java.time.LocalDate;
import java.util.Optional;

public interface InbodyRepository {

    Inbody save(Inbody inbody);

    boolean existsByUserIdAndMeasuredDate(Long userId, LocalDate measuredDate);

    InbodySlice findInbodySlice(
            Long userId,
            LocalDate measuredDate,
            Long inbodyId,
            int size
    );

    Optional<Inbody> findByIdAndUserId(Long inbodyId, Long userId);

    void deleteById(Long inbodyId);
}
