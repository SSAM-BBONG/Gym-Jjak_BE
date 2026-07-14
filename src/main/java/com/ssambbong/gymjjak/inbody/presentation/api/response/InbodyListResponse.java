package com.ssambbong.gymjjak.inbody.presentation.api.response;

import java.time.LocalDate;
import java.util.List;

public record InbodyListResponse(
        List<InbodyItemResponse> inbodies,
        LocalDate nextMeasuredDate,
        Long nextInbodyId,
        boolean hasNext
) {
}
