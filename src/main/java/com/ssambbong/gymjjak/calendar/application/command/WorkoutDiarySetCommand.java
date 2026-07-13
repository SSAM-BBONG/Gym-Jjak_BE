package com.ssambbong.gymjjak.calendar.application.command;

import java.math.BigDecimal;

public record WorkoutDiarySetCommand(
        Integer setOrder,
        BigDecimal weight,
        Integer reps
) {
}
