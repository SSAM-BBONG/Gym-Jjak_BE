package com.ssambbong.gymjjak.onboarding.application.command;

import java.math.BigDecimal;

public record RegionCommand(
        String sido,
        String sigungu,
        String eupmyeondong,
        String fullName,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
