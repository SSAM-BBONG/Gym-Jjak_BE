package com.ssambbong.gymjjak.inbody.domain.repository;

import com.ssambbong.gymjjak.inbody.domain.model.Inbody;

import java.time.LocalDate;
public interface InbodyRepository {

    Inbody save(Inbody inbody);

    boolean existsByUserIdAndMeasuredDate(Long userId, LocalDate measuredDate);
}
