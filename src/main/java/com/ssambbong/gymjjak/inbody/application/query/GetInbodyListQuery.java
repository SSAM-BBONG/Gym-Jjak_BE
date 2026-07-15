package com.ssambbong.gymjjak.inbody.application.query;

import java.time.LocalDate;

public record GetInbodyListQuery(
        Long userId,
        LocalDate measuredDate,
        Long inbodyId
) {
}
