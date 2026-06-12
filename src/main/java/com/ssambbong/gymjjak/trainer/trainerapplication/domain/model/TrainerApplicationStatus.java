package com.ssambbong.gymjjak.trainer.trainerapplication.domain.model;

public enum TrainerApplicationStatus {
    // 대기 상태
    PENDING,
    // 승인 상태
    APPROVED,
    // 반려
    REJECTED,
    // 직접 취소
    CANCELED
}
