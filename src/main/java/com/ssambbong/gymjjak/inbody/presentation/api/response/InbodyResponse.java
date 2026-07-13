package com.ssambbong.gymjjak.inbody.presentation.api.response;

import com.ssambbong.gymjjak.inbody.domain.model.BmiStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InbodyResponse(
        Long inbodyId,
        LocalDate measuredDate,
        BigDecimal height,
        BigDecimal weight,
        BigDecimal bodyFatPercentage,
        BigDecimal skeletalMuscleMass,
        BigDecimal bmi,
        BmiStatus bmiStatus,
        String bmiStatusDescription,
        BigDecimal weightChangeRate
) {
}
