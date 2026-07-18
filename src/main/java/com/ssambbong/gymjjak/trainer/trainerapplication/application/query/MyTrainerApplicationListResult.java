package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import java.util.List;

// 트레이너 신청서 목록 조회
public record MyTrainerApplicationListResult(
        List<MyTrainerApplicationSummaryResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}
