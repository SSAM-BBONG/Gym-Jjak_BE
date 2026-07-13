package com.ssambbong.gymjjak.pt.ptReservation.domain.model;

public enum PtSessionStatus {
    RESERVED,   // 예약됨 (reserved_end_at >= NOW())
    COMPLETED,  // 수강완료 (reserved_end_at < NOW())
    CANCELLED   // 취소
}
