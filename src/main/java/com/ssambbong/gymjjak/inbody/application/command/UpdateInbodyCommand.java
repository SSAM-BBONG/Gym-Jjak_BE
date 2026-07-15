package com.ssambbong.gymjjak.inbody.application.command;


import java.math.BigDecimal;

public record UpdateInbodyCommand(
        Long userId,
        BigDecimal height,
        BigDecimal weight,
        BigDecimal bodyFatPercentage,
        BigDecimal skeletalMuscleMass
) {
}
