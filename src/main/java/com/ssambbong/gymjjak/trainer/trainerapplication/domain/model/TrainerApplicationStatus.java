package com.ssambbong.gymjjak.trainer.trainerapplication.domain.model;

import java.util.List;

public enum TrainerApplicationStatus {
    // 대기 상태
    PENDING,
    // 승인 상태
    APPROVED,
    // 반려
    REJECTED,
    // 직접 취소
    CANCELED;

    public static List<TrainerApplicationStatus> getDuplicateBlockingStatuses() {
        return List.of(PENDING, APPROVED);
    }
}
