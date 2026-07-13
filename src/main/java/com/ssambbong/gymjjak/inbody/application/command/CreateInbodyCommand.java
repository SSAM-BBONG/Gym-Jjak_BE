package com.ssambbong.gymjjak.inbody.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInbodyCommand(
        Long userId,
        LocalDate measuredDate,
        BigDecimal height,
        BigDecimal weight,
        BigDecimal bodyFatPercentage,
        BigDecimal skeletalMuscleMass
) {
}
