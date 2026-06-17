package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import java.util.List;

// 페이지 목록 Result
public record TrainerApplicationListResult(
        List<TrainerApplicationSummaryResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}
