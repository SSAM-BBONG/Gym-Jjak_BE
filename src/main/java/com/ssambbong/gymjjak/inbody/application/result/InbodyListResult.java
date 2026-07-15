package com.ssambbong.gymjjak.inbody.application.result;

import java.time.LocalDate;
import java.util.List;

public record InbodyListResult(
        List<InbodyItemResult> inbodies,
        LocalDate nextMeasuredDate,
        Long nextInbodyId,
        boolean hasNext
) {
}
